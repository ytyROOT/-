package com.example.chat;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private Node parentNode;
    private  String text;
    private  String value;
    private int icon=-1;
    private List<Node>children=new ArrayList<>();
    private boolean isExpanded=true;
    private boolean isOnline=false;
    Node(String m_text,String m_value)
    {
        this.text = m_text;
        this.value = m_value;
    }

    public void setOnline(boolean bOnline)
    {
        isOnline = bOnline;
    }

    public boolean getOnline()
    {
        return isOnline;
    }

    public String getText()
    {
        return this.text;
    }

    public void setText(String newText)
    {
        this.text = newText;
    }

    public String getValue()
    {
        return this.value;
    }

    public void setValue(String newValue)
    {
        this.value = newValue;
    }

    public void setIcon(int icon)
    {
        this.icon = icon;
    }

    public int getIcon()
    {
        return this.icon;
    }

    public void setParentNode(Node node)
    {
        this.parentNode = node;
    }

    public Node getParent()
    {
        return this.parentNode;
    }

    public boolean isRoot()
    {
        return this.parentNode==null?true:false;
    }

    public int getLevel()
    {
        return this.parentNode==null?0:this.parentNode.getLevel()+1;
    }

    public List<Node>getChildren()
    {
        return this.children;
    }

    public void add(Node node)
    {
        if(!children.contains(node))
            this.children.add(node);
    }

    public void remove(Node node)
    {
        if(children.contains(node))
            this.children.remove(node);
    }

    public void remove(int location)
    {
        this.children.remove(location);
    }

    public void clear()
    {
        this.children.clear();
    }

    public boolean isLeaf()
    {
        return this.children.size()<1?true:false;
    }

    public boolean isExpanded()
    {
        return this.isExpanded;
    }

    public void setExpanded(boolean isExpanded)
    {
        this.isExpanded = isExpanded;
    }

    public boolean isParentCollaspsed()
    {
        if(this.parentNode==null) return !isExpanded;
        if(!this.parentNode.isExpanded())
            return true;
        return this.parentNode.isParentCollaspsed();
    }
}
