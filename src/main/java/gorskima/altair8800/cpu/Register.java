package gorskima.altair8800.cpu;

public enum Register {
	A(1,0), F(1,1), B(1,2), C(1,3), D(1,4), E(1,5), H(1,6), L(1,7),
	AF(2,A.offset), BC(2,B.offset), DE(2,D.offset), HL(2,H.offset),
	SP(2,8), PC(2,10);
	
	public final int size;
	public final int offset;

	Register(final int size, final int addr) {
		this.size = size;
		this.offset = addr;
	}
}
