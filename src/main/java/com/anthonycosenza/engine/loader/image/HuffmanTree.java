package com.anthonycosenza.engine.loader.image;

import java.util.PriorityQueue;

public class HuffmanTree
{
    private Node head;
    private Node pointer;
    private int pathCounter;
    
    public HuffmanTree(int[] alphabet, int[] frequencies)
    {
        if(alphabet.length != frequencies.length) throw new RuntimeException("Alphabet length(" + alphabet.length + ") doesn't match frequencies length(" + frequencies.length +")");
        
        PriorityQueue<Node> queue = new PriorityQueue<>();
        
        //Add all character nodes to tree.
        for(int i = 0; i < alphabet.length; i++)
        {
            queue.add(new Node(alphabet[i], frequencies[i], null, null));
        }
    
        /*
         * Continuously combining nodes left to right(lowest to highest frequency) until we have a completed tree.
         * When combining 2 nodes their frequency/magnitude gets combined and they get sorted in the queue,
         * so this algorithm is constantly combining the smallest leafs and internals until only the root node is left.
         */
        while(queue.size() > 1)
        {
            Node node1 = queue.poll();
            Node node2 = queue.poll();
        
            queue.add(new Node(node1.freq + (node2 == null ? 0 : node2.freq), node1, node2));
        }
        //No longer need the queue.
        head = queue.poll();
        pointer = head;
        pathCounter = 0;
    }
    
    /*
     * When traversing a binary tree, left is considered 0(false) and right is 1(true)
     */
    public Node traverseTree(boolean right)
    {
        Node node = right ? pointer.right : pointer.left;
    
        pathCounter++;
        
        if(node != null)
        {
            if(node.hasData)
            {
                pointer = head;
                System.out.println("PathCounter: " + pathCounter);
                pathCounter = 0;
                return node;
            }
            else
            {
                pointer = node;
                return null;
            }
            
        }
        else throw new RuntimeException("Node has no " + (right ? "right path" : "left path") + " and has no data assigned.");
    }
    
    @Override
    public String toString()
    {
        return "HuffmanTree{" + head + '}';
    }
    
    public static class Node implements Comparable<Node>
    {
        boolean hasData;
        int data;
        int freq;
        
        Node left;
        Node right;
    
        public Node(int freq, Node left, Node right)
        {
            this.hasData = false;
            this.data = 0;
            this.freq = freq;
            this.left = left;
            this.right = right;
        }
        public Node(int data, int freq, Node left, Node right)
        {
            this.hasData = true;
            this.data = data;
            this.freq = freq;
            this.left = left;
            this.right = right;
        }
        public Node(char data, int freq, Node left, Node right)
        {
            this((int) data, freq, left, right);
        }
        
        @Override
        public int compareTo(Node other)
        {
            //Sort by frequency and if they share a frequency, then sort by data value.
            return this.freq == other.freq ? this.data - other.data : this.freq - other.freq;
        }
        
        @Override
        public String toString()
        {
            return "Node{" +
                    "data=" + data +
                    ", freq=" + freq +
                    ", left=" + left +
                    ", right=" + right +
                    '}';
        }
    }
}
