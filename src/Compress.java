import java.io.*;

public class Compress {

    public static long time;

    static void compress(String path) throws IOException {
        File file = new File(path);
        try (OutputStream out = new BufferedOutputStream(new FileOutputStream(path + ".zip"))) {
            compress(file, "", out);
        }
    }

    private static void compress(File file, String pathOfFile, OutputStream out) {
        try {
            pathOfFile += file.getName();
            DataOutputStream dos = new DataOutputStream(out);

            if (file.isDirectory()) {
                if (file.listFiles().length == 0) {
                    dos.writeUTF(pathOfFile);
                    dos.writeInt(-1);
                } else
                    pathOfFile += "/";
                for (File inFile : file.listFiles()) {
                    compress(inFile, pathOfFile, out);
                }
            } else {
                dos.writeUTF(pathOfFile);

                //emptyFile;
                if (file.length() == 0) {
                    dos.writeInt(0);
                } else {

                    InputStream in = new BufferedInputStream(new FileInputStream(file));

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    OutStream result = new OutStream(baos);
                    int chars;
                    while ((chars = in.read()) != -1) {
                        result.write(chars);
                    }
                    in.close();
                    result.close();

                    dos.writeInt(baos.size());
                    baos.writeTo(out);
                    baos.close();
                }
            }
            System.out.print("Finish compressing!");
        } catch (Exception e) {
            System.out.print("Fail to Compress the File");
        }
    }

    public long getTime() {
        time = System.currentTimeMillis();
        return time;
    }
}

class OutStream extends OutputStream {
    private ByteArrayOutputStream baos = new ByteArrayOutputStream();
    private DataOutputStream data;

    OutStream(OutputStream out) throws IOException {
        this.data = new DataOutputStream(out);
    }

    public void write(int chars) throws IOException {
        baos.write(chars);
    }

    public void close() throws IOException {
        byte[] input = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(input);
        Frequency freq = new Frequency(bais);
        bais.close();

        Huffman huffTree = new Huffman(freq);
        huffTree.writeEncoding(data);

        BitOutputStream bos = new BitOutputStream(data);

        int len = input.length;

        for (int i = 0; i < len; i++)
            bos.writeBits(huffTree.Code((input[i]) & 0xff));
        bos.writeBits(huffTree.Code(256));

        bos.close();
        baos.close();
    }
}


class BitOutputStream {
    private OutputStream out;
    private int buffer;
    private int pos;

    BitOutputStream(OutputStream out) {
        this.pos = this.buffer = 0;
        this.out = out;
    }

    void writeBits(int[] bits) throws IOException {
        int len = bits.length;
        for (int i = 0; i < len; i++) {
            writeBit(bits[i]);
        }
    }

    private void writeBit(int bits) throws IOException {
        buffer = setBit(buffer, pos++, bits);
        if (pos == 8)
            flush();
    }

    private int setBit(int buffer, int pos, int bits) {
        if (bits == 1)
            buffer |= (bits << pos);
        return buffer;
    }

    private void flush() throws IOException {
        if (pos == 0)
            return;
        out.write(buffer);
        pos = 0;
        buffer = 0;
    }

    public void close() throws IOException {
        flush();
        out.close();
    }
}
