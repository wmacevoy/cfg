package cfg;

// http://stackoverflow.com/questions/760228/how-do-you-merge-two-input-streams-in-java

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;

public class CatInputStream extends InputStream {
    private final Deque<InputStream> streams;

    public CatInputStream(InputStream... streams) {
        this.streams = new LinkedList<InputStream>();
        Collections.addAll(this.streams, streams);
    }

    private void nextStream() throws IOException {
        streams.removeFirst().close();
    }

    @Override
        public int read() throws IOException {
        int result = -1;
        while (!streams.isEmpty()
               && (result = streams.getFirst().read()) == -1) {
            nextStream();
        }
        return result;
    }

    @Override
        public int read(byte b[], int off, int len) throws IOException {
        int result = -1;
        while (!streams.isEmpty()
               && (result = streams.getFirst().read(b, off, len)) == -1) {
            nextStream();
        }
        return result;
    }

    @Override
        public long skip(long n) throws IOException {
        long skipped = 0L;
        while (skipped < n && !streams.isEmpty()) {
            long thisSkip = streams.getFirst().skip(n - skipped);
            if (thisSkip > 0)
                skipped += thisSkip;
            else
                nextStream();
        }
        return skipped;
    }

    @Override
        public int available() throws IOException {
        return streams.isEmpty() ? 0 : streams.getFirst().available();
    }

    @Override
        public void close() throws IOException {
        while (!streams.isEmpty())
            nextStream();
    }
}