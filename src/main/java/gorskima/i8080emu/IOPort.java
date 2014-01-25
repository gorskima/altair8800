package gorskima.i8080emu;

public interface IOPort {

	int read();
	void write(int n);
	
}
