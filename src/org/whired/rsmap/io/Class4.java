package org.whired.rsmap.io;

public class Class4
{

	public static byte method67(Class5 class5)
	{
		return (byte)method74(8, class5);
	}

	public static void method68(Class5 class5)
	{
		int k8 = 0;
		int ai[] = null;
		int ai1[] = null;
		int ai2[] = null;
		class5.anInt69 = 1;
		if(Class5.anIntArray78 == null)
			Class5.anIntArray78 = new int[class5.anInt69 * 0x186a0];
		boolean flag20 = true;
		while(flag20) 
		{
			byte byte0 = method67(class5);
			if(byte0 == 23)
				return;
			byte0 = method67(class5);
			byte0 = method67(class5);
			byte0 = method67(class5);
			byte0 = method67(class5);
			byte0 = method67(class5);
			class5.anInt70++;
			byte0 = method67(class5);
			byte0 = method67(class5);
			byte0 = method67(class5);
			byte0 = method67(class5);
			byte0 = method71(class5);
			if(byte0 != 0)
				class5.aBoolean66 = true;
			else
				class5.aBoolean66 = false;
			if(class5.aBoolean66)
				System.out.println("PANIC! RANDOMISED BLOCK!");
			class5.anInt71 = 0;
			byte0 = method67(class5);
			class5.anInt71 = class5.anInt71 << 8 | byte0 & 0xff;
			byte0 = method67(class5);
			class5.anInt71 = class5.anInt71 << 8 | byte0 & 0xff;
			byte0 = method67(class5);
			class5.anInt71 = class5.anInt71 << 8 | byte0 & 0xff;
			for(int j = 0; j < 16; j++)
			{
				byte byte1 = method71(class5);
				if(byte1 == 1)
					class5.aBooleanArray81[j] = true;
				else
					class5.aBooleanArray81[j] = false;
			}

			for(int k = 0; k < 256; k++)
				class5.aBooleanArray80[k] = false;

			for(int l = 0; l < 16; l++)
				if(class5.aBooleanArray81[l])
				{
					for(int i3 = 0; i3 < 16; i3++)
					{
						byte byte2 = method71(class5);
						if(byte2 == 1)
							class5.aBooleanArray80[l * 16 + i3] = true;
					}

				}

			method70(class5);
			int i4 = class5.anInt79 + 2;
			int j4 = method74(3, class5);
			int k4 = method74(15, class5);
			for(int i1 = 0; i1 < k4; i1++)
			{
				int j3 = 0;
				do
				{
					byte byte3 = method71(class5);
					if(byte3 == 0)
						break;
					j3++;
				} while(true);
				class5.aByteArray86[i1] = (byte)j3;
			}

			byte abyte0[] = new byte[6];
			for(byte byte16 = 0; byte16 < j4; byte16++)
				abyte0[byte16] = byte16;

			for(int j1 = 0; j1 < k4; j1++)
			{
				byte byte17 = class5.aByteArray86[j1];
				byte byte15 = abyte0[byte17];
				for(; byte17 > 0; byte17--)
					abyte0[byte17] = abyte0[byte17 - 1];

				abyte0[0] = byte15;
				class5.aByteArray85[j1] = byte15;
			}

			for(int k3 = 0; k3 < j4; k3++)
			{
				int l6 = method74(5, class5);
				for(int k1 = 0; k1 < i4; k1++)
				{
					do
					{
						byte byte4 = method71(class5);
						if(byte4 == 0)
							break;
						byte4 = method71(class5);
						if(byte4 == 0)
							l6++;
						else
							l6--;
					} while(true);
					class5.aByteArrayArray87[k3][k1] = (byte)l6;
				}

			}

			for(int l3 = 0; l3 < j4; l3++)
			{
				byte byte8 = 32;
				int i = 0;
				for(int l1 = 0; l1 < i4; l1++)
				{
					if(class5.aByteArrayArray87[l3][l1] > i)
						i = class5.aByteArrayArray87[l3][l1];
					if(class5.aByteArrayArray87[l3][l1] < byte8)
						byte8 = class5.aByteArrayArray87[l3][l1];
				}

				method72(class5.anIntArrayArray88[l3], class5.anIntArrayArray89[l3], class5.anIntArrayArray90[l3], class5.aByteArrayArray87[l3], byte8, i, i4);
				class5.anIntArray91[l3] = byte8;
			}

			int l4 = class5.anInt79 + 1;
			int i5 = -1;
			int j5 = 0;
			for(int i2 = 0; i2 <= 255; i2++)
				class5.anIntArray74[i2] = 0;

			int l9 = 4095;
			for(int l8 = 15; l8 >= 0; l8--)
			{
				for(int j9 = 15; j9 >= 0; j9--)
				{
					class5.aByteArray83[l9] = (byte)(l8 * 16 + j9);
					l9--;
				}

				class5.anIntArray84[l8] = l9 + 1;
			}

			int i6 = 0;
			if(j5 == 0)
			{
				i5++;
				j5 = 50;
				byte byte12 = class5.aByteArray85[i5];
				k8 = class5.anIntArray91[byte12];
				ai = class5.anIntArrayArray88[byte12];
				ai2 = class5.anIntArrayArray90[byte12];
				ai1 = class5.anIntArrayArray89[byte12];
			}
			j5--;
			int i7 = k8;
			int l7;
			byte byte9;
			for(l7 = method74(i7, class5); l7 > ai[i7]; l7 = l7 << 1 | byte9)
			{
				i7++;
				byte9 = method71(class5);
			}

			for(int k5 = ai2[l7 - ai1[i7]]; k5 != l4;)
				if(k5 == 0 || k5 == 1)
				{
					int j6 = -1;
					int k6 = 1;
					do
					{
						if(k5 == 0)
							j6 += 1 * k6;
						else
						if(k5 == 1)
							j6 += 2 * k6;
						k6 *= 2;
						if(j5 == 0)
						{
							i5++;
							j5 = 50;
							byte byte13 = class5.aByteArray85[i5];
							k8 = class5.anIntArray91[byte13];
							ai = class5.anIntArrayArray88[byte13];
							ai2 = class5.anIntArrayArray90[byte13];
							ai1 = class5.anIntArrayArray89[byte13];
						}
						j5--;
						int j7 = k8;
						int i8;
						byte byte10;
						for(i8 = method74(j7, class5); i8 > ai[j7]; i8 = i8 << 1 | byte10)
						{
							j7++;
							byte10 = method71(class5);
						}

						k5 = ai2[i8 - ai1[j7]];
					} while(k5 == 0 || k5 == 1);
					j6++;
					byte byte5 = class5.aByteArray82[class5.aByteArray83[class5.anIntArray84[0]] & 0xff];
					class5.anIntArray74[byte5 & 0xff] += j6;
					for(; j6 > 0; j6--)
					{
						Class5.anIntArray78[i6] = byte5 & 0xff;
						i6++;
					}

				} else
				{
					int j11 = k5 - 1;
					byte byte6;
					if(j11 < 16)
					{
						int j10 = class5.anIntArray84[0];
						byte6 = class5.aByteArray83[j10 + j11];
						for(; j11 > 3; j11 -= 4)
						{
							int k11 = j10 + j11;
							class5.aByteArray83[k11] = class5.aByteArray83[k11 - 1];
							class5.aByteArray83[k11 - 1] = class5.aByteArray83[k11 - 2];
							class5.aByteArray83[k11 - 2] = class5.aByteArray83[k11 - 3];
							class5.aByteArray83[k11 - 3] = class5.aByteArray83[k11 - 4];
						}

						for(; j11 > 0; j11--)
							class5.aByteArray83[j10 + j11] = class5.aByteArray83[(j10 + j11) - 1];

						class5.aByteArray83[j10] = byte6;
					} else
					{
						int l10 = j11 / 16;
						int i11 = j11 % 16;
						int k10 = class5.anIntArray84[l10] + i11;
						byte6 = class5.aByteArray83[k10];
						for(; k10 > class5.anIntArray84[l10]; k10--)
							class5.aByteArray83[k10] = class5.aByteArray83[k10 - 1];

						class5.anIntArray84[l10]++;
						for(; l10 > 0; l10--)
						{
							class5.anIntArray84[l10]--;
							class5.aByteArray83[class5.anIntArray84[l10]] = class5.aByteArray83[(class5.anIntArray84[l10 - 1] + 16) - 1];
						}

						class5.anIntArray84[0]--;
						class5.aByteArray83[class5.anIntArray84[0]] = byte6;
						if(class5.anIntArray84[0] == 0)
						{
							int i10 = 4095;
							for(int i9 = 15; i9 >= 0; i9--)
							{
								for(int k9 = 15; k9 >= 0; k9--)
								{
									class5.aByteArray83[i10] = class5.aByteArray83[class5.anIntArray84[i9] + k9];
									i10--;
								}

								class5.anIntArray84[i9] = i10 + 1;
							}

						}
					}
					class5.anIntArray74[class5.aByteArray82[byte6 & 0xff] & 0xff]++;
					Class5.anIntArray78[i6] = class5.aByteArray82[byte6 & 0xff] & 0xff;
					i6++;
					if(j5 == 0)
					{
						i5++;
						j5 = 50;
						byte byte14 = class5.aByteArray85[i5];
						k8 = class5.anIntArray91[byte14];
						ai = class5.anIntArrayArray88[byte14];
						ai2 = class5.anIntArrayArray90[byte14];
						ai1 = class5.anIntArrayArray89[byte14];
					}
					j5--;
					int k7 = k8;
					int j8;
					byte byte11;
					for(j8 = method74(k7, class5); j8 > ai[k7]; j8 = j8 << 1 | byte11)
					{
						k7++;
						byte11 = method71(class5);
					}

					k5 = ai2[j8 - ai1[k7]];
				}

			class5.anInt65 = 0;
			class5.aByte64 = 0;
			class5.anIntArray76[0] = 0;
			for(int j2 = 1; j2 <= 256; j2++)
				class5.anIntArray76[j2] = class5.anIntArray74[j2 - 1];

			for(int k2 = 1; k2 <= 256; k2++)
				class5.anIntArray76[k2] += class5.anIntArray76[k2 - 1];

			for(int l2 = 0; l2 < i6; l2++)
			{
				byte byte7 = (byte)(Class5.anIntArray78[l2] & 0xff);
				Class5.anIntArray78[class5.anIntArray76[byte7 & 0xff]] |= l2 << 8;
				class5.anIntArray76[byte7 & 0xff]++;
			}

			class5.anInt72 = Class5.anIntArray78[class5.anInt71] >> 8;
			class5.anInt75 = 0;
			class5.anInt72 = Class5.anIntArray78[class5.anInt72];
			class5.anInt73 = (byte)(class5.anInt72 & 0xff);
			class5.anInt72 >>= 8;
			class5.anInt75++;
			class5.anInt92 = i6;
			method69(class5);
			if(class5.anInt75 == class5.anInt92 + 1 && class5.anInt65 == 0)
				flag20 = true;
			else
				flag20 = false;
		}
	}

	public static void method69(Class5 class5)
	{
		byte byte4 = class5.aByte64;
		int i = class5.anInt65;
		int j = class5.anInt75;
		int k = class5.anInt73;
		int ai[] = Class5.anIntArray78;
		int l = class5.anInt72;
		byte abyte0[] = class5.aByteArray59;
		int i1 = class5.anInt60;
		int j1 = class5.anInt61;
		int k1 = j1;
		int l1 = class5.anInt92 + 1;
label0:
		do
		{
			if(i > 0)
			{
				do
				{
					if(j1 == 0)
						break label0;
					if(i == 1)
						break;
					abyte0[i1] = byte4;
					i--;
					i1++;
					j1--;
				} while(true);
				if(j1 == 0)
				{
					i = 1;
					break;
				}
				abyte0[i1] = byte4;
				i1++;
				j1--;
			}
			boolean flag = true;
			while(flag) 
			{
				flag = false;
				if(j == l1)
				{
					i = 0;
					break label0;
				}
				byte4 = (byte)k;
				l = ai[l];
				byte byte0 = (byte)(l & 0xff);
				l >>= 8;
				j++;
				if(byte0 != k)
				{
					k = byte0;
					if(j1 == 0)
					{
						i = 1;
					} else
					{
						abyte0[i1] = byte4;
						i1++;
						j1--;
						flag = true;
						continue;
					}
					break label0;
				}
				if(j != l1)
					continue;
				if(j1 == 0)
				{
					i = 1;
					break label0;
				}
				abyte0[i1] = byte4;
				i1++;
				j1--;
				flag = true;
			}
			i = 2;
			l = ai[l];
			byte byte1 = (byte)(l & 0xff);
			l >>= 8;
			if(++j != l1)
				if(byte1 != k)
				{
					k = byte1;
				} else
				{
					i = 3;
					l = ai[l];
					byte byte2 = (byte)(l & 0xff);
					l >>= 8;
					if(++j != l1)
						if(byte2 != k)
						{
							k = byte2;
						} else
						{
							l = ai[l];
							byte byte3 = (byte)(l & 0xff);
							l >>= 8;
							j++;
							i = (byte3 & 0xff) + 4;
							l = ai[l];
							k = (byte)(l & 0xff);
							l >>= 8;
							j++;
						}
				}
		} while(true);
		int i2 = class5.anInt62;
		class5.anInt62 += k1 - j1;
		if(class5.anInt62 < i2)
			class5.anInt63++;
		class5.aByte64 = byte4;
		class5.anInt65 = i;
		class5.anInt75 = j;
		class5.anInt73 = k;
		Class5.anIntArray78 = ai;
		class5.anInt72 = l;
		class5.aByteArray59 = abyte0;
		class5.anInt60 = i1;
		class5.anInt61 = j1;
	}

	public static void method70(Class5 class5)
	{
		class5.anInt79 = 0;
		for(int i = 0; i < 256; i++)
			if(class5.aBooleanArray80[i])
			{
				class5.aByteArray82[class5.anInt79] = (byte)i;
				class5.anInt79++;
			}

	}

	public static byte method71(Class5 class5)
	{
		return (byte)method74(1, class5);
	}

	public static void method72(int ai[], int ai1[], int ai2[], byte abyte0[], int i, int j, int k)
	{
		int l = 0;
		for(int i1 = i; i1 <= j; i1++)
		{
			for(int l2 = 0; l2 < k; l2++)
				if(abyte0[l2] == i1)
				{
					ai2[l] = l2;
					l++;
				}

		}

		for(int j1 = 0; j1 < 23; j1++)
			ai1[j1] = 0;

		for(int k1 = 0; k1 < k; k1++)
			ai1[abyte0[k1] + 1]++;

		for(int l1 = 1; l1 < 23; l1++)
			ai1[l1] += ai1[l1 - 1];

		for(int i2 = 0; i2 < 23; i2++)
			ai[i2] = 0;

		int i3 = 0;
		for(int j2 = i; j2 <= j; j2++)
		{
			i3 += ai1[j2 + 1] - ai1[j2];
			ai[j2] = i3 - 1;
			i3 <<= 1;
		}

		for(int k2 = i + 1; k2 <= j; k2++)
			ai1[k2] = (ai[k2 - 1] + 1 << 1) - ai1[k2];

	}

	public static int method73(byte abyte0[], int i, byte abyte1[], int j, int k)
	{
		synchronized(aClass5_43)
		{
			aClass5_43.aByteArray54 = abyte1;
			aClass5_43.anInt55 = k;
			aClass5_43.aByteArray59 = abyte0;
			aClass5_43.anInt60 = 0;
			aClass5_43.anInt56 = j;
			aClass5_43.anInt61 = i;
			aClass5_43.anInt68 = 0;
			aClass5_43.anInt67 = 0;
			aClass5_43.anInt57 = 0;
			aClass5_43.anInt58 = 0;
			aClass5_43.anInt62 = 0;
			aClass5_43.anInt63 = 0;
			aClass5_43.anInt70 = 0;
			method68(aClass5_43);
			i -= aClass5_43.anInt61;
			int l = i;
			return l;
		}
	}

	public static int method74(int i, Class5 class5)
	{
		int j;
		do
		{
			if(class5.anInt68 >= i)
			{
				int k = class5.anInt67 >> class5.anInt68 - i & (1 << i) - 1;
				class5.anInt68 -= i;
				j = k;
				break;
			}
			class5.anInt67 = class5.anInt67 << 8 | class5.aByteArray54[class5.anInt55] & 0xff;
			class5.anInt68 += 8;
			class5.anInt55++;
			class5.anInt56--;
			class5.anInt57++;
			if(class5.anInt57 == 0)
				class5.anInt58++;
		} while(true);
		return j;
	}

	public static Class5 aClass5_43 = new Class5();

}
