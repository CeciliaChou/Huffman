import java.io.*;

class Uncompress {
    public static long time;

    static void uncompress(String compressedFile) {
        try {
            String folder = "";
            int split = compressedFile.lastIndexOf("/");

            folder = compressedFile.substring(0, split + 1);

            InputStream in = new BufferedInputStream(new FileInputStream(compressedFile));
            DataInputStream data = new DataInputStream(in);

            while (data.available() > 0) {
                String pathOfFile = data.readUTF();
                int len = data.readInt();
                File dest = new File(folder + pathOfFile);
                createDir(dest);
                switch (len) {
                    case -1:
                        if (!dest.exists()) {
                            dest.mkdir();
                        }
                        break;
                    case 0:
                        if (!dest.exists()) {
                            dest.createNewFile();
                        }
                        break;
                    default:
                        dest.createNewFile();
                        byte[] b = new byte[len];
                        data.read(b);
                        InStream stream = new InStream(new ByteArrayInputStream(b));
                        FileOutputStream out = new FileOutputStream(dest);
                        int ch;
                        byte[] temp = new byte[65536];
                        int pos = 0;
                        while ((ch = stream.read()) != -1) {
                            temp[pos] = (byte) ch;
                            pos++;
                            if (pos == 65536) {
                                out.write(temp);
                                pos = 0;
                            }
                        }
                        out.write(temp, 0, pos);
                        stream.close();
                        out.close();
                }
            }
            System.out.print("Finish Depressing!");
        } catch (Exception e) {
            System.out.print("Fail to Depress the zipFile");
        }
    }

    private static void createDir(File file) {
        file = file.getParentFile();
        if (file.isDirectory())
            return;
        createDir(file);
        file.mkdir();
    }

    public long getTime() {
        time = System.currentTimeMillis();
        return time;
    }
}

class InStream {
    private BitInputStream inputStream;
    private Huffman huffTree;

    InStream(InputStream ins) throws IOException {
        DataInputStream dis = new DataInputStream(ins);
        huffTree = new Huffman();
        huffTree.readEncoding(dis);
        inputStream = new BitInputStream(ins);
    }

    public int read() throws IOException {
        String bits = "";
        int bit;
        int decode;
        while (true) {
            bit = inputStream.readBit();
            if (bit == -1)
                throw new IOException("Unexpected exception");
            bits += bit;
            decode = huffTree.Value(bits);
            if (decode == Huffman.TEMP_NODE)
                continue;
            else if (decode == Huffman.END)
                return -1;
            else
                return decode;
        }
    }

    public void close() throws IOException {
        inputStream.close();
    }
}

class BitInputStream {
    private InputStream ins;
    private int buffer;
    private int pos;

    BitInputStream(InputStream ins) {
        this.ins = ins;
        pos = 8;
    }

    int readBit() throws IOException {
        if (pos == 8) {
            buffer = ins.read();
            if (buffer == -1)
                return -1;
            pos = 0;
        }
        return getBit(buffer, pos++);
    }

    public void close() throws IOException {
        ins.close();
    }

    private static int getBit(int buffer, int pos) {
        return (buffer & (1 << pos)) != 0 ? 1 : 0;
    }
}
