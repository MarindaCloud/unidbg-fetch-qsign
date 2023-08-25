package com.tencent.mobileqq.qsec.qsecest;

import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;

public class SelfBase64 {

    public static class Encoder {

        private final boolean doPadding;
        private final boolean isURL;
        private final int linemax;
        private final byte[] newline;
        private static final String base64chars = "EBnuvwxCD+FGHIopqrstJKLRSTUlmyz012VWXYZaMNOPQbcdefghijk3456789A/";
        private static final char[] toBase64 = base64chars.toCharArray();
        private static final char[] toBase64URL = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '_'};
        private static final byte[] CRLF = {13, 10};
        public static final Encoder RFC4648 = new Encoder(false, null, -1, true);
        static final Encoder RFC4648_URLSAFE = new Encoder(true, null, -1, true);
        private static final int MIMELINEMAX = 76;
        static final Encoder RFC2045 = new Encoder(false, CRLF, MIMELINEMAX, true);

        private Encoder(boolean z, byte[] bArr, int i, boolean z2) {
            this.isURL = z;
            this.newline = bArr;
            this.linemax = i;
            this.doPadding = z2;
        }

        private final int outLength(int i) {
            int i2;
            if (this.doPadding) {
                i2 = ((i + 2) / 3) * 4;
            } else {
                int i3 = i % 3;
                i2 = (i3 == 0 ? 0 : i3 + 1) + ((i / 3) * 4);
            }
            if (this.linemax > 0) {
                return i2 + (((i2 - 1) / this.linemax) * this.newline.length);
            }
            return i2;
        }

        public byte[] encode(byte[] bArr) {
            try {
                byte[] bArr2 = new byte[outLength(bArr.length)];
                int encode0 = encode0(bArr, 0, bArr.length, bArr2);
                if (encode0 != bArr2.length) {
                    return Arrays.copyOf(bArr2, encode0);
                }
                return bArr2;
            } catch (Throwable th) {
                th.printStackTrace();
                return null;
            }
        }

        public ByteBuffer encode(ByteBuffer byteBuffer) {
            int encode0;
            byte[] bArr = new byte[outLength(byteBuffer.remaining())];
            if (byteBuffer.hasArray()) {
                encode0 = encode0(byteBuffer.array(), byteBuffer.arrayOffset() + byteBuffer.position(), byteBuffer.arrayOffset() + byteBuffer.limit(), bArr);
                byteBuffer.position(byteBuffer.limit());
            } else {
                byte[] bArr2 = new byte[byteBuffer.remaining()];
                byteBuffer.get(bArr2);
                encode0 = encode0(bArr2, 0, bArr2.length, bArr);
            }
            return ByteBuffer.wrap(encode0 != bArr.length ? Arrays.copyOf(bArr, encode0) : bArr);
        }

        public int encode(byte[] bArr, byte[] bArr2) {
            if (bArr2.length < outLength(bArr.length)) {
                throw new IllegalArgumentException("Output byte array is too small for encoding all input bytes");
            }
            return encode0(bArr, 0, bArr.length, bArr2);
        }

        public String encodeToString(byte[] bArr) {
            try {
                byte[] encode = encode(bArr);
                return new String(encode, 0, 0, encode.length);
            } catch (Throwable th) {
                return null;
            }
        }

        public OutputStream wrap(OutputStream outputStream) {
            //Objects.requireNonNull(outputStream);
            //return new SelfBase64.EncOutputStream(outputStream, this.isURL ? toBase64URL : toBase64, this.newline, this.linemax, this.doPadding);
            return null;
        }

        public Encoder withoutPadding() {
            return !this.doPadding ? this : new Encoder(this.isURL, this.newline, this.linemax, false);
        }

        private int encode0(byte[] bArr, int i, int i2, byte[] bArr2) {
            int i3;
            char[] cArr = this.isURL ? toBase64URL : toBase64;
            int i4 = ((i2 - i) / 3) * 3;
            int i5 = i + i4;
            if (this.linemax > 0 && i4 > (this.linemax / 4) * 3) {
                i4 = (this.linemax / 4) * 3;
            }
            int i6 = 0;
            int i7 = i;
            while (i7 < i5) {
                int min = Math.min(i7 + i4, i5);
                int i8 = i6;
                int i9 = i7;
                while (i9 < min) {
                    int i10 = i9 + 1;
                    int i11 = i10 + 1;
                    int i12 = ((bArr[i10] & 255) << 8) | ((bArr[i9] & 255) << 16);
                    i9 = i11 + 1;
                    int i13 = i12 | (bArr[i11] & 255);
                    int i14 = i8 + 1;
                    bArr2[i8] = (byte) cArr[(i13 >>> 18) & 63];
                    int i15 = i14 + 1;
                    bArr2[i14] = (byte) cArr[(i13 >>> 12) & 63];
                    int i16 = i15 + 1;
                    bArr2[i15] = (byte) cArr[(i13 >>> 6) & 63];
                    i8 = i16 + 1;
                    bArr2[i16] = (byte) cArr[i13 & 63];
                }
                int i17 = ((min - i7) / 3) * 4;
                i6 += i17;
                if (i17 == this.linemax && min < i2) {
                    byte[] bArr3 = this.newline;
                    int length = bArr3.length;
                    int i18 = 0;
                    while (i18 < length) {
                        bArr2[i6] = bArr3[i18];
                        i18++;
                        i6++;
                    }
                }
                i7 = min;
            }
            if (i7 < i2) {
                int i19 = i7 + 1;
                int i20 = bArr[i7] & 255;
                int i21 = i6 + 1;
                bArr2[i6] = (byte) cArr[i20 >> 2];
                if (i19 == i2) {
                    i3 = i21 + 1;
                    bArr2[i21] = (byte) cArr[(i20 << 4) & 63];
                    if (this.doPadding) {
                        int i22 = i3 + 1;
                        bArr2[i3] = 61;
                        int i23 = i22 + 1;
                        bArr2[i22] = 61;
                        return i23;
                    }
                } else {
                    int i24 = i19 + 1;
                    int i25 = bArr[i19] & 255;
                    int i26 = i21 + 1;
                    bArr2[i21] = (byte) cArr[((i20 << 4) & 63) | (i25 >> 4)];
                    i3 = i26 + 1;
                    bArr2[i26] = (byte) cArr[(i25 << 2) & 63];
                    if (this.doPadding) {
                        int i27 = i3 + 1;
                        bArr2[i3] = 61;
                        return i27;
                    }
                }
                return i3;
            }
            return i6;
        }
    }
}
