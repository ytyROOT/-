package com.example.chat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class Fragementechat extends Fragment implements AdapterView.OnItemClickListener{

    static ListView contactlist=null;
    String contactlistjson="";
    String usertypejson="";
    Node root = null;
    String myTag="MytTag";
    Message msg_send;
    private ArrayList<User> AddFriendList=new ArrayList<User>();
    private View v;

    public Fragementechat()
    {

    }

    @Override
    public View onCreateView( LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.activity_friendlist, container, false);
        contactlist=(ListView)v.findViewById(R.id.userlist);
        contactlist.setOnItemClickListener(this);


        new Thread(rstart).start();

        return v;
    }

    Runnable r=new Runnable() {
        @Override
        public void run() {
            DataBaseCon dbc=new DataBaseCon();

            contactlistjson=dbc.getMyFriendListJsonString(MyApp.instance().getUserId());
            usertypejson=dbc.getUserTypeJsonString();

            parseJson(contactlistjson);

            LoadNodes();

            Bundle bundle = new Bundle();

            bundle.putString("message", "登陆成功");

            Message msg_send = new Message();

            msg_send.setData(bundle);

            h1.sendMessage(msg_send);

        }
    };

    Handler h1 = new Handler()
    {
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);

            Bundle bd = msg.getData();

            String message = bd.getString("message");

            if(message.equals("登陆成功"))
            {
                Log.i("MySql11:", "4" );
                TreeAdapter treeAdapter = new TreeAdapter(v.getContext(), root);
                contactlist.setAdapter(treeAdapter);

                h.postDelayed(refreshUserStatus,1000);
            }
        }
    };

    private JSONArray parseJson(String jsonString)
    {
        JSONArray jsonArray=null;

        try{
            jsonArray=new JSONArray(jsonString);

        }catch (Exception e){
            Log.i(myTag,"数组1"+e.toString());
        }
        return jsonArray;

    }

    void LoadNodes() {
        root = new Node("好友列表", "000000");

        JSONArray persons = parseJson(contactlistjson);
        JSONArray usertype = parseJson(usertypejson);

        Node[] alltypeNode = new Node[usertype.length()];

        Node[] allNode = new Node[persons.length()];
        try {
            for (int i = 0; i < usertype.length(); i++) {
                JSONObject usertypejsonobiect = usertype.getJSONObject(i);
                String id = usertypejsonobiect.getString("id");
                String caption = usertypejsonobiect.getString("caption");

                Log.i(myTag, id + "-" + caption);
                alltypeNode[i] = new Node(caption, id);
            }
        } catch (Exception e) {
            Log.i(myTag, "错误2" + e.toString());
        }

        Node[] allPersonNode = new Node[persons.length()];
        try {
            for (int i = 0; i < persons.length(); i++) {
                JSONObject jsonObject = persons.getJSONObject(i);
                String myid = jsonObject.getString("myid");
                String friendid = jsonObject.getString("friendid");
                String nickname = jsonObject.getString("nickname");
                String usertypeid = jsonObject.getString("usertypeid");
                String typename = jsonObject.getString("typename");
                String online = jsonObject.getString("online");
                Log.i(myTag, myid + "-" + friendid + "-" + nickname + "-" + online + "-" + usertypeid + "-" + typename + "-");
                allPersonNode[i] = new Node(nickname, usertypeid + "-" + friendid + "-" + myid);
            }
        } catch (Exception e) {
            Log.i(myTag, "错误3" + e.toString());
        }

        for (int j = 0; j < allPersonNode.length; j++) {
            String value = allPersonNode[j].getValue();
            String usertypeid = value.split("-")[0];
            int foundid = 0;
            Log.i(myTag, value + "-" + usertypeid);
            for (int i = 0; i < alltypeNode.length; i++) {
                if (alltypeNode[i].getValue().equals(usertypeid)) {
                    foundid = i;
                    break;
                }
            }
            allPersonNode[j].setParentNode(alltypeNode[foundid]);
            alltypeNode[foundid].add(allPersonNode[j]);
        }

        for (int i = 0; i < alltypeNode.length; i++) {
            alltypeNode[i].setParentNode(root);
            root.add(alltypeNode[i]);
        }

    }
    Runnable refreshUserStatus=new Runnable() {
        @Override
        public void run() {
            new Thread(subrefreshUserStatus).start(); //在线程调用其他的线程 其他的线程成了非主线程 subrefreshUserStatus
            h.postDelayed(this,1000);
        }
    };



    Runnable subrefreshUserStatus=new Runnable() {
        @Override
        public void run() {
            //数据库的相关操作
            DataBaseCon dbc=new DataBaseCon();
            contactlistjson=dbc.getMyFriendListJsonString(MyApp.instance().getUserId());
            Message msg=new Message();
            Bundle bd=new Bundle();
            bd.putString("listjon",contactlistjson);
            msg.setData(bd);
            Log.i("setOnline","online1");
            h.sendMessage(msg);
        }
    };
    //定时器 接收来别的线程发过来的信息
    Handler h=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bd=new Bundle();
            bd=msg.getData();
            String json=bd.getString("listjon");
            //对json字符串进行解析
            JSONArray persons=parseJson(json);
            Log.i("setOnline","online2");
            try
            {
                TreeAdapter ta= (TreeAdapter)contactlist.getAdapter();
                for(int i=0;i<persons.length();i++)
                {
                    JSONObject jsonObject=persons.getJSONObject(i);
                    String myid=jsonObject.getString("myid");
                    String friendid=jsonObject.getString("friendid");
                    //String friendname=jsonObject.getString("friendname");
                    String nickname=jsonObject.getString("nickname");
                    String usertypeid=jsonObject.getString("usertypeid");
                    String typename=jsonObject.getString("typename");
                    String online=jsonObject.getString("online");
                    Log.i("myTag",myid+"-"+friendid+"-"+nickname+"-"+usertypeid+"-"+typename);
                    //改写节点的online
                    String keyvalue=usertypeid+"-"+friendid+"-"+myid;
                    int p=ta.getPositionByValue(keyvalue);
                    if(p>0)
                    {
                        Node x=(Node)ta.getItem(p);
                        Log.i("setOnline","online");
                        x.setOnline(online.equals("1")?true:false);
                        ta.notifyDataSetChanged();
                    }
                }
            }
            catch (Exception e)
            {
                Log.i("setOnline", "Error" + e);
            }
        }
    };


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TreeAdapter ta=(TreeAdapter)parent.getAdapter();
        ta.ExpandAndCollapse(position);

        Node x=(Node)ta.getItem(position);
        if(x.isLeaf())
        {
            // myTag
            Intent intent=new Intent(v.getContext(),ChatMainActivity.class);
            Bundle bd=new Bundle(  );
            bd.putString("friendid",x.getValue().split( "-" )[1] );
            bd.putString( "friendnickname",x.getText() );
            intent.putExtras( bd );
            startActivity(intent);

        }
    }

    Handler processFriendList=new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.i("AddFriend", "3");
            if(msg.what==1)
            {
                String myid=MyApp.instance().getUserId();
                if(0!=AddFriendList.size())
                {
                    for(int i = 0; i <AddFriendList.size();i++) {

                        User u = AddFriendList.get(i);
                        int friendid = u.getUserid();
                        String nickname = u.getUserNickName();
                        Log.i("AddFriend", "friendid:" + friendid);
                        Log.i("AddFriend", "friendid:" + nickname);
                        AlertDialog ad = new AlertDialog.Builder(v.getContext()).create();

                        Log.i("AddFriend", "5");
                        ad.setTitle("提示");
                        ad.setMessage("是否将" + nickname + "加为好友？");
                        ad.setButton(DialogInterface.BUTTON_POSITIVE, "添加", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MyApp.instance().updataBySql("update myfriend set valid=1,usertypeid=1 where myid=" + myid + " and friendid=" + friendid + ";");
                                new Thread(r).start();
                            }
                        });
                        ad.setButton(DialogInterface.BUTTON_NEGATIVE, "不添加", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MyApp.instance().updataBySql("delete from myfriend where myid=" + myid + " and friendid=" + friendid + ";");
                                new Thread(r).start();
                            }
                        });

                        ad.show();
                    }
                }
                else
                {
                    new Thread(r).start();
                }


            }
        }
    };

    Runnable runnableProcessFriendList=new Runnable() {
        @Override
        public void run() {
            Log.i("AddFriend", "2");
            searctUsers("select myid,friendid,(select nickname from users where users.sid=A.friendid) " +
                    "as nickname from myfriend A where myid="+MyApp.instance().getUserId()+" and valid=0");
            Message msg = new Message();
            msg.what=1;
            processFriendList.sendMessage(msg);
        }
    };

    Runnable rstart=new Runnable() {
        @Override
        public void run() {
            Log.i("AddFriend", "1");
            new Thread(runnableProcessFriendList).start();
        }
    };


    public void searctUsers(String m_sql)
    {
        DataBaseCon dbc = new DataBaseCon();
        String ret="";
        ResultSet rs=null;

        Connection con = dbc.getCon();
        try
        {
            Statement st = con.createStatement();
            rs=(ResultSet)st.executeQuery(m_sql);
            AddFriendList=new ArrayList<User>();

            while (rs.next())
            {
                int sid=rs.getInt("friendid");
                String nickname=rs.getString("nickname");
                AddFriendList.add(new User(sid,nickname));
            }
            st.close();
            con.close();;
        }
        catch (Exception e)
        {
            Log.i("Search", "Error:" + e);
        }
    }
}
