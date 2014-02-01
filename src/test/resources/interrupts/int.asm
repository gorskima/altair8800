	org 0000h

	lxi sp,0200h
	ei
	jmp loop

	
	org 0008h

	inr a
	ei
	ret
	
	org 0100h
loop:
	cpi 3
	jz done
	jmp loop

done:
	hlt
