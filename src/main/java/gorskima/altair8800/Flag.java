package gorskima.altair8800;

public enum Flag {
	S(0x80), Z(0x40), H(0x10), P(0x04), C(0x01);

	public final int mask;

	Flag(final int value) {
		this.mask = value;
	}

}
