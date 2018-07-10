import java.io.*;
import java.util.*;

class Node {
    int value;
    int weight;
    Node left, right;
    Node parent;

    Node(int value, int weight) {
        this.value = value;
        this.weight = weight;
        left = right = null;
        parent = null;
    }
}

class Frequency {
    private int counts[] = new int[256];

    Frequency() {
    }

    Frequency(InputStream input) throws IOException {
        int chars;
        while ((chars = input.read()) != -1) {
            counts[chars]++;
        }
    }

    int getFrequency(int chars) {
        return counts[chars & 0xff];
    }

    void setFrequency(int chars, int count) {
        this.counts[chars & 0xff] = count;
    }
}


class Huffman {

    private Frequency counts;
    private Node root;
    private Node[] nodes = new Node[257];

    static final int TEMP_NODE = -1;
    static final int END = 256;

    Huffman() {
        counts = new Frequency();
        root = null;
    }

    Huffman(Frequency counts) {
        this.counts = counts;
        root = null;
        buildTree();
    }

    private void buildTree() {
        PriorityQueue<Node> queue = new PriorityQueue<>(257, new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                return o1.weight - o2.weight;
            }
        });

        for (int i = 0; i < 256; i++) {
            if (counts.getFrequency(i) != 0) {
                Node node = new Node(i, counts.getFrequency(i));
                nodes[i] = node;
                queue.add(node);
            }
        }

        //handle the end of the input stream;
        nodes[END] = new Node(END, 1);
        queue.add(nodes[END]);

        while (queue.size() > 1) {
            Node node1 = queue.poll();
            Node node2 = queue.poll();
            Node node = new Node(TEMP_NODE, node1.weight + node2.weight);
            node.left = node1;
            node.right = node2;
            node1.parent = node2.parent = node;
            queue.add(node);
        }
        root = queue.poll();
    }

    //get huffman code;
    public int[] Code(int chars) {
        Node cur = nodes[chars];
        if (cur == null)
            return null;
        String str = "";
        Node parent = cur.parent;
        while (parent != null) {
            if (parent.left == cur)
                str = "0" + str;
            else
                str = "1" + str;
            cur = cur.parent;
            parent = cur.parent;
        }

        int len = str.length();
        int[] code = new int[len];
        for (int i = 0; i < len; i++) {
            if (str.charAt(i) == '0')
                code[i] = 0;
            else
                code[i] = 1;
        }
        return code;
    }

    //get huffman value
    public int Value(String code) {
        Node cur = root;
        int len = code.length();
        for (int i = 0; cur != null && i < len; i++)
            if (code.charAt(i) == '0')
                cur = cur.left;
            else
                cur = cur.right;
        if (cur == null)
            return -1;
        return cur.value;
    }


    void writeEncoding(DataOutputStream out) throws IOException {
        for (int i = 0; i < 256; i++) {
            if (counts.getFrequency(i) > 0) {
                out.writeByte(i);
                out.writeInt(counts.getFrequency(i));
            }
        }
        out.writeByte(0);
        out.writeInt(0);
    }

    void readEncoding(DataInputStream in) throws IOException {
        for (int i = 0; i < 256; i++)
            counts.setFrequency(i, 0);
        byte chars;
        int number;
        for (; ; ) {
            chars = in.readByte();
            number = in.readInt();
            if (number == 0)
                break;
            counts.setFrequency(chars, number);
        }
        buildTree();
    }
}

