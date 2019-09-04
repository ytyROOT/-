package com.example.chat;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class TreeAdapter extends BaseAdapter{
    private Context con;
    private LayoutInflater lif;
    private List<Node> allNode=new ArrayList<>();
    private int expandIcon;
    private int collapseIcon;
    private List<Node>allNodeCache=new ArrayList<Node>();

    TreeAdapter(Context m_con,Node rootNode)
    {
        con=m_con;
        this.lif=(LayoutInflater)con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        addNode(rootNode);
    }

    private void addNode(Node node)
    {
        allNode.add(node);
        allNodeCache.add(node);
        if(node.isLeaf())
            return;
        for(int i=0;i<node.getChildren().size();i++)
            addNode(node.getChildren().get(i));
    }

    @Override
    public int getCount() {
        return allNode.size();
    }

    @Override
    public Object getItem(int position) {
        return allNode.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        Log.i("View", "List:" + convertView);
        if(convertView==null)
        {
            convertView=this.lif.inflate(R.layout.listview_item_tree,null);
            viewHolder=new ViewHolder();
            viewHolder.ivIcon1=(ImageView)convertView.findViewById(R.id.imageView1);
            viewHolder.ivIcon2=(ImageView)convertView.findViewById(R.id.imageView2);
            viewHolder.tvText=(TextView)convertView.findViewById(R.id.textView1);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder=(ViewHolder)convertView.getTag();
        }
        Node x=allNode.get(position);
        if(x!=null)
        {
            Log.i("List", "ListText:" + x.getText().toCharArray());
            viewHolder.tvText.setText(x.getText().toCharArray(),0,x.getText().toCharArray().length);
            if(x.isLeaf())
            {
                if(x.getOnline())
                {
                    viewHolder.ivIcon1.setImageResource(R.drawable.offline);
                }
                else
                {
                    viewHolder.ivIcon1.setImageResource(R.drawable.online);
                }
            }
            else{
                if(x.isExpanded())
                {
                    viewHolder.ivIcon1.setImageResource(R.drawable.on);
                }
                else viewHolder.ivIcon1.setImageResource(R.drawable.off);
                //viewHolder.ivIcon1.setImageResource(R.drawable.on);
            }
            convertView.setPadding(x.getLevel()*120,2,2,2);
        }
        return convertView;
    }

    private class ViewHolder
    {
        ImageView ivIcon1;
        TextView tvText;
        ImageView ivIcon2;
    }
    public void ExpandAndCollapse(int position)
    {
        Node x=allNode.get(position);
        if(x!=null)
        {
            if(!x.isLeaf())
            {
                x.setExpanded(!x.isExpanded());
                filterNode();
                this.notifyDataSetChanged();
            }
        }
    }
    private void filterNode()
    {
        allNode.clear();
        for (int i=0;i<allNodeCache.size();i++)
        {
            Node x=allNodeCache.get(i);
            if(!x.isParentCollaspsed()||x.isRoot())
                allNode.add(x);
        }
    }
    public int getPositionByValue(String value)
    {
        int ret = 0;
        for(int i = 0;i < allNode.size();i++)
        {
            if(allNode.get(i).getValue().equals(value))
            {
                ret=i;
                break;
            }
        }

        return ret;
    }

    public void setCollapseAndExpandedIcon(int expandedIcon,int collapseIcon)
    {
        this.expandIcon=expandedIcon;
        this.collapseIcon=collapseIcon;
    }

}