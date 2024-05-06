package com.anthonycosenza.engine.loader.image;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class HuffmanModel
{
    private List<Character> chars;
    private List<Integer> freqs;
    private List<Integer> bitLength;
    private PriorityQueue<Node> queue;
    private Node head;
    
    public HuffmanModel(int[] lengths, int[] code)
    {
        queue = new PriorityQueue<>();
        //split(data);
        bitLength = new ArrayList<>();
        
        //Add all character nodes to tree.
        for(int i = 0; i < chars.size(); i++)
        {
            queue.add(new Node(chars.get(i), freqs.get(i), null, null));
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
            
            queue.add(new Node('\0', node1.freq + (node2 == null ? 0 : node2.freq), node1, node2));
        }
        
        head = queue.peek();
    }
    public HuffmanModel(String data)
    {
        queue = new PriorityQueue<>();
        split(data);
        bitLength = new ArrayList<>();
    
        //Add all character nodes to tree.
        for(int i = 0; i < chars.size(); i++)
        {
            queue.add(new Node(chars.get(i), freqs.get(i), null, null));
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
            
            queue.add(new Node('\0', node1.freq + (node2 == null ? 0 : node2.freq), node1, node2));
        }
        
        head = queue.peek();
    }
    private String decode(String bitString)
    {
        StringBuilder builder = new StringBuilder();
        int pointer = 0;
        while(pointer < bitString.length())
        {
            Node node = head;
            while(node.data == '\0')
            {
                char bit = bitString.charAt(pointer++);
                if(bit == '0')
                {
                    node = node.left;
                }
                else
                {
                    node = node.right;
                }
            }
            builder.append(node.data);
        }
        return builder.toString();
    }
    
    
    private void sort()
    {
        //Sort by frequency which should also sort by bit length.
        for(int i = 0; i < chars.size(); i++)
        {
            int smallest = freqs.get(i);
        
            for(int j = i; j < chars.size(); j++)
            {
                if(freqs.get(j) < smallest)
                {
                    char c = chars.get(i);
                    int bl = bitLength.get(i);
                    chars.set(i, chars.get(j));
                    freqs.set(i, freqs.get(j));
                    bitLength.set(i, bitLength.get(j));
                
                    chars.set(j, c);
                    freqs.set(j, smallest);
                    bitLength.set(j, bl);
                
                    smallest = freqs.get(i);
                }
            }
        }
    }
    
    private void split(String data)
    {
        chars = new ArrayList<>();
        freqs = new ArrayList<>();
        
        for(int i = 0; i < data.length(); i++)
        {
            char character = data.charAt(i);
            int charIndex = chars.indexOf(character);
            
            //If the char doesnt exist then we add a new char and add a new frequency.
            if(charIndex == -1)
            {
                chars.add(character);
                freqs.add(1);
            }
            else //Otherwise increase the frequency count by 1
            {
                freqs.set(charIndex, freqs.get(charIndex) + 1);
            }
        }
    }
    
    
    public static class Node implements Comparable<Node>
    {
        char data;
        int freq;
        
        Node left;
        Node right;
    
        
        public Node(char data, int freq, Node left, Node right)
        {
            this.data = data;
            this.freq = freq;
            this.left = left;
            this.right = right;
        }
    
        @Override
        public int compareTo(Node other)
        {
            return this.freq - other.freq;
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
