package gorskima.altair8800.cpu;

import gorskima.altair8800.Word;

public interface IOPort {

	Word read();
	void write(Word n);
	
}
