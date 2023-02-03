import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class DocumentIterator implements Iterator<String> {

    private Reader r;
    private int    c = -1;
    private int n;
    

    public DocumentIterator(Reader r, int n) {
        this.n = n;
        this.r = r;
        skipNonLetters();
    }


    private void skipNonLetters() {
        try {
            this.c = this.r.read();
            while (!Character.isLetter(this.c) && this.c != -1) {
                this.c = this.r.read();
            }
        } catch (IOException e) {
            this.c = -1;
        }
    }


    @Override
    public boolean hasNext() {
        return (c != -1);
    }


    @Override
    public String next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        String answer = "";
        try {
        
            for (int i = 0; i < n; i++) {
                while (Character.isLetter(this.c)) {
                    answer = answer + Character.toLowerCase(((char)this.c));
                    this.c = this.r.read();
                }
                if (i == 0) {
                    this.r.mark(1000);
                }
                
                skipNonLetters();
                
                if (c == -1) {
                    this.r.mark(1000);
                }
                
            }
            //reset the reader to be the last char read
            this.r.reset();
            //skip non letters, so the reader is just at the first char of next word
            skipNonLetters();
        } catch (IOException e) {
            throw new NoSuchElementException();
        }

        return answer;
    }

}
