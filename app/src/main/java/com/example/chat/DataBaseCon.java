package com.example.chat;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DataBaseCon {

    String myTag;

    public Connection getCon()
    {

        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            String username="root";
            String password="";

           Connection con = DriverManager.getConnection("jdbc:mysql://192.168.1.232:3306/mystudent?characterEncoding=utf-8", "root", "");
           // Connection con = DriverManager.getConnection("jdbc:mysql://47.103.199.14:3306/mystudent?characterEncoding=utf-8", "root", "duroc1234");
            Log.i("MySql:", "连接成功:" );

            return  con;
        }
        catch (Exception e)
        {
          //  Log.i("MySql:", "连接失败" );
            Log.i("MySql:", "连接失败:" + e );
         }

        return null;
    }

    public int getCount(String sql) {
        int nCount = 0;
        ResultSet rs = null;


        try
        {
            Connection con = getCon();
            if(null != con)
            {
                Statement st = con.createStatement();
                rs = (ResultSet)st.executeQuery(sql);
                rs.last();
                nCount = rs.getRow();
                st.close();
                con.close();;
            }

        }
        catch (SQLException e)
        {
            Log.i("MySql:", "getExcetion" + e);
        }

        Log.i("MySql:", "Count:" + nCount);
        return nCount;
    }

    public boolean ExecQuery(String sql) {
        boolean ret=false;
        Connection con=getCon();
        try{
            Statement st=con.createStatement();
            st.execute(sql);
            st.close();
            con.close();
            ret=true;
        }
        catch (Exception e){
            Log.i("Logout:", "Error:" + e);
        }
        return ret;
    }

    public String searchBySql(String sql,String FieldName)
    {
        String ret="";
        ResultSet rs=null;
        Connection con=getCon();
        try{
            Statement st=con.createStatement();
            rs=(ResultSet)st.executeQuery(sql);
            while (rs.next())
            {
                int sid=rs.getInt(FieldName);

                ret=""+sid;
            }
            st.close();
            con.close();

            Log.i("MySql1", "Error1" + ret);

        }catch (Exception e){
            Log.i("MySql1", "Error:" + e);
        }
        return ret;
    }

    public String getMyFriendListJsonString(String myid)
    {
        String ret="";
        String sql="select myid,friendid,(select nickname from users where users.sid=A.friendid) " +
                "as nickname,(select online from users where users.sid=A.friendid) as online, usertypeid,(select caption from usertype where usertype.id=A.usertypeid) "+
                "as typename from myfriend A where myid='"+myid+"' and valid = 1 order by usertypeid asc";

        ResultSet rs=null;
        Log.i( myTag,"Data 116"+rs );
        Connection con=getCon();
        try{
            //创建一个可执行sql语句的陈述对象
            Statement st=con.createStatement();
            Log.i( myTag,"Data 120"+rs );
            rs=(ResultSet)st.executeQuery(sql);//获得结果集
            Log.i( myTag,"Data 122"+rs );
            //遍历结果集

            while (rs.next())
            {
                String friendid=""+rs.getInt("friendid");

                String nickname=""+rs.getString("nickname");

                String usertypeid=""+rs.getInt("usertypeid");

                String typename=""+rs.getString("typename");

                String online=""+rs.getString("online");

                if(ret=="")
                    ret+="{\"myid\":\""+myid+"\",\"friendid\":\""+friendid+"\",\"nickname\":\""+nickname+"\",\"online\":\""+online+"\",\"usertypeid\":\""+usertypeid+"\",\"typename\":\""+typename+"\"}";
                else
                    ret+=","+"{\"myid\":\""+myid+"\",\"friendid\":\""+friendid+"\",\"nickname\":\""+nickname+"\",\"online\":\""+online+"\",\"usertypeid\":\""+usertypeid+"\",\"typename\":\""+typename+"\"}";
            }
            if(ret!="")
            {
                ret="["+ret+"]";
            }

            st.close();
            con.close();
        }
        catch (Exception e){
            Log.i("MyTag:", "错误" + e);
        }
        return ret;
    }
    public String getUserTypeJsonString()
    {
        String ret="";
        String sql="select id,caption from usertype order by id asc";
        ResultSet rs=null;
        Connection con=getCon();
        try{
            //创建一个可执行sql语句的陈述对象
            Statement st=con.createStatement();
            rs=(ResultSet)st.executeQuery(sql);//获得结果集
            //遍历结果集
            while (rs.next())
            {
                String id=""+rs.getInt("id");
                String caption=""+rs.getString("caption");
                if(ret=="")
                    ret+="{\"id\":\""+id+"\",\"caption\":\""+caption+"\"}";
                else
                    ret+=","+"{\"id\":\""+id+"\",\"caption\":\""+caption+"\"}";
            }
            if(ret!="")
            {ret="["+ret+"]";}

            st.close();
            con.close();
        }
        catch (Exception e){}
        return ret;
    }


//    public String getNewUsers(String sql){
//        String ret="";
//        Log.i(myTag,"Reg 184 sql:"+sql);
//        ResultSet rs=null;
//        Connection con=getCon();
//        try{
//            //创建一个可执行sql语句的陈述对象
//            Statement st=con.createStatement();
//            rs=(ResultSet)st.executeQuery(sql);//获得结果集
//            //遍历结果集
//
//
//            st.close();
//            con.close();
//        }
//        catch (Exception e){
//
//        }
//        return ret;
//    }
}
