package com.example.chat;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class Fragementdis extends Fragment implements AdapterView.OnItemClickListener{

    ListView lv;
    Button searchButton;
    EditText searchTextCtrl;
    String myId="";
    int searctUserid=0;

    public Fragementdis()
    {

    }

    
    @Override
    public View onCreateView( LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View v = (View)inflater.inflate(R.layout.activity_contact, container, false);
        lv=(ListView)v.findViewById(R.id.lstFriends);
        searchButton=(Button)v.findViewById(R.id.button1);
        searchTextCtrl=(EditText)v.findViewById(R.id.editText1);
        myId=MyApp.instance().getUserId();
        String sql = "select sid,nickname from users where sid <> "+myId+" and sid not in (select friendid from myfriend where myid="+myId+")";
        adapter1 a = new adapter1(v.getContext(),sql);
        lv.setAdapter(a);
        lv.setOnItemClickListener(this);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = searchTextCtrl.getText().toString().trim();
                if(searchText.equals(""))
                {
                    Toast.makeText(v.getContext(), "搜索关键词不能为空",Toast.LENGTH_LONG).show();
                }
                else {
                    String conditional="locate('"+searchText+"',qq)>0 or locate('"+searchText+"',email)>0 or locate('"+searchText+"',address)>0 or locate('"+searchText+"',mobile)>0 or locate('"+searchText+"',nickname)>0 ";
                    String sql="select sid,nickname from users where ("+conditional+") and sid <> "+myId+" and sid not in (select friendid from myfriend where myid="+myId+");";
                    adapter1 ad = (adapter1) lv.getAdapter();
                    ad.setSql(sql);
                    ad.startSearch();
                };
            }
        });
        return v;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        try{
            adapter1 ad=(adapter1)parent.getAdapter();
            User u = (User)ad.getItem(position);
            int userid = u.getUserid();
            String usernickname = u.getUserNickName();
            MyApp.instance().updataBySql("insert into myfriend(myid,friendid,usertypeid,valid) values ('"+ myId +"','"+userid+"','0','0');");
            Toast.makeText(view.getContext(),"好友请求已经发送给"+usernickname,Toast.LENGTH_SHORT).show();
            ad.startSearch();
        }
        catch (Exception e)
        {

        }
    }

    private class ViewHolder
    {
        ImageView ivIcon1;
        TextView tvText;
        ImageView ivIcon2;
    }

    class adapter1 extends BaseAdapter
    {
        private LayoutInflater lif;
        ArrayList<User> userList = new ArrayList<User>();
        private String m_sql="";

        @Override
        public int getCount() {
            return userList.size();
        }

        adapter1(Context con, String sql)
        {
            this.m_sql=sql;
            this.lif=(LayoutInflater)con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            startSearch();
        }

        public void setSql(String sql)
        {
            this.m_sql=sql;
        }

        public void startSearch()
        {
            new Thread(r).start();
        }

        Handler h = new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what==1)
                {
                    dataRefresh();
                }
            }
        };

        public void dataRefresh()
        {
            this.notifyDataSetChanged();
        }

        Runnable r = new Runnable() {
            @Override
            public void run() {
                searctUsers();
                Message msg = new Message();
                msg.what=1;
                h.sendMessage(msg);
            }
        };

        public void searctUsers()
        {
            DataBaseCon dbc = new DataBaseCon();
            String ret="";
            ResultSet rs=null;

            Connection con = dbc.getCon();
            try
            {
                Statement st = con.createStatement();
                rs=(ResultSet)st.executeQuery(m_sql);
                userList=new ArrayList<User>();

                while (rs.next())
                {
                    int sid=rs.getInt("sid");
                    String nickname=rs.getString("nickname");
                    userList.add(new User(sid,nickname));
                }
                st.close();
                con.close();;
            }
            catch (Exception e)
            {
                Log.i("Search", "Error:" + e);
            }
        }

        @Override
        public Object getItem(int position) {
            return userList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder=null;
            if(convertView==null)
            {
                convertView=this.lif.inflate(R.layout.listview_item_tree,null);
                holder=new ViewHolder();
                holder.ivIcon1=(ImageView)convertView.findViewById(R.id.imageView1);
                holder.ivIcon2=(ImageView)convertView.findViewById(R.id.imageView2);
                holder.tvText=(TextView)convertView.findViewById(R.id.textView1);
                convertView.setTag(holder);
            }
            else
            {
                holder=(ViewHolder)convertView.getTag();
            }
            User n=userList.get(position);
            holder.tvText.setText(n.nickname);
            convertView.setPadding(35,2,2,2);
            return convertView;
        }
    }
}
