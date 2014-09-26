package gorskima.altair8800.io;

import gorskima.altair8800.Word;

public interface SerialDevice {

	Word read();

	void write(Word n);

}
