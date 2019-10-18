/* =================================================================== *
 * Вариант реализации функции генерации уникального идентификатора     *
 * договора (сделки)  в соответствии с указанием Банка России          *
 * "О правилах присвоения уникального идентификатора договора (сделки),*
 * по обязательствам из которого (из которой) формируется кредитная    *
 * история"                                                            *
 * =================================================================== */
package com.uuid;

import java.io.*;
import java.text.*;
import java.util.*;

/**
* UUID 128-битный универсально уникальный идентификатор
*
*	   
    *      
    *      timeLow(ui4) - Четыре октета, младшие биты значения времени (32 бита)
    *      
    *      timeMid(ui2) - Два октета, средние биты значения времени (16 битов) 
    *      
    *      timeHiAndVersion(ui2) - Два октета, 4 бита с последующими старшими битами значения времени (12 битов)
    *      
    *      clockSeqHiAndReserved(ui1) - Один октет, Биты варианта ( 2 бита ) с последующими старшими битами временной последовательности (6 битов)
    *      
    *      clockSeqLow(ui1) - Один октет , младшие биты временной последовательности
    *      
    *      nodeId (u_i1[6]) - Шесть октетов, Узел 48 битов 
    *      
*
*/


public class UUID implements Comparable, Serializable
{
    public static final int STRING_LENGTH = 36;

    private int 	timeLow;
    private short	timeMid;
    private short 	timeHiAndVersion;
    private byte	clockSeqHiAndReserved;
    private byte 	clockSeqLow;
    private byte	nodeId[];

    public static void main(String[] args) {
        String main_uuid;
        char ctrl_uuid;
        String res;
        UUID u = new UUID();
        main_uuid = u.toString().toLowerCase();
        ctrl_uuid = calc_ctrl(u.toString());
        res = main_uuid + "-" + ctrl_uuid;
        System.out.println(res);
    }
    
    // контрольная сумма
    public static char calc_ctrl(String str) {
        str = str.toLowerCase();
        long sum = 0;
        int index = 1; 
        for(int i = 0; i < str.toCharArray().length; i++)
        {
           char ch = str.toCharArray()[i];
            if(ch>='0' && ch<='9'){
                    //увеличение суммы и индекса
                    sum+=(ch-'0')*(index++);
            }
            if(ch>='a' && ch<='f'){
                    //увеличение суммы и индекса
                    sum+=(ch-'a'+10)*(index++);
            }
            if(index>10) index=1;           
           
        }

        int r = (int) (sum % 16);
        if (r<10){
		return (char)(r+'0');
	} else {
		return (char)(r+'a'-10);
	}        
    }
    
    /**
    * Generate a new UUID
    */
    public UUID() {
	nodeId = new byte[6];
	this.generate();
    }

    public UUID(String str) throws NumberFormatException {
	StringTokenizer st = new StringTokenizer(str, "-", true);
	nodeId = new byte[6];
	try
	{
	    timeLow = (int)Long.parseLong(st.nextToken(), 16);
	    if (!st.nextToken().equals("-"))
		throw new NumberFormatException();
	    timeMid = (short)Integer.parseInt(st.nextToken(), 16);
	    if (!st.nextToken().equals("-"))
		throw new NumberFormatException();
	    timeHiAndVersion = (short)Integer.parseInt(st.nextToken(), 16);
	    if (!st.nextToken().equals("-"))
		throw new NumberFormatException();
	    short clock = (short)Integer.parseInt(st.nextToken(), 16);
	    clockSeqHiAndReserved = (byte)((clock >> 8) & 0xFF);
	    clockSeqLow = (byte)(clock & 0xFF);
	    if (!st.nextToken().equals("-"))
		throw new NumberFormatException();
	    String node = st.nextToken();
	    for (int i = 0; i < 6; i++)
	    {
		nodeId[i] = (byte)Integer.parseInt(
					node.substring(i*2, i*2+2), 16);
	    }
	}
	catch (Exception ex)
	{
	    throw new NumberFormatException();
	}
    }
	    
    private void generate()
    {   
	UUIDGenerator gener = UUIDGenerator.create();
	gener.generate(this);
    }

    /*
    * Конвертирование UUID в строку формата
    * XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX
    * timeLow-timeMid-timeHiAndVersion-clockSeqAndReserved-nodeId

    * @return String version of UUID
    */
    public String toString()
    {
	Object nodeArgs[] = {
	    toHex(nodeId[0]), toHex(nodeId[1]), toHex(nodeId[2]),
	    toHex(nodeId[3]), toHex(nodeId[4]), toHex(nodeId[5])
			    };		
	String nodeString = 
	    MessageFormat.format("{0}{1}{2}{3}{4}{5}", nodeArgs);

	Object args[] = {
	    toHex(timeLow), toHex(timeMid), toHex(timeHiAndVersion),
	    toHex(clockSeqHiAndReserved), toHex(clockSeqLow),
	    nodeString
			};		

	return MessageFormat.format("{0}-{1}-{2}-{3}{4}-{5}", args);
    }

    public boolean equals(Object o)
    {
	if (!(o instanceof UUID))
	    return false;

	UUID other = (UUID)o;
	return (
	    other.timeLow == this.timeLow &&
	    other.timeMid == this.timeMid &&
	    other.timeHiAndVersion == this.timeHiAndVersion &&
	    other.clockSeqHiAndReserved == this.clockSeqHiAndReserved &&
	    other.clockSeqLow == this.clockSeqLow &&
	    other.nodeId[0] == this.nodeId[0] &&
	    other.nodeId[1] == this.nodeId[1] &&
	    other.nodeId[2] == this.nodeId[2] &&
	    other.nodeId[3] == this.nodeId[3] &&
	    other.nodeId[4] == this.nodeId[4] &&
	    other.nodeId[5] == this.nodeId[5]);
    }

    public int compareTo(Object o)
    {
	if (!(o instanceof UUID))
	    throw new ClassCastException();

	UUID other = (UUID)o;
	if (other.timeLow != this.timeLow)
	    return this.timeLow - other.timeLow;
	if (other.timeMid != this.timeMid)
	    return this.timeMid - other.timeMid;
	if (other.timeHiAndVersion != this.timeHiAndVersion)
	    return this.timeHiAndVersion - other.timeHiAndVersion;
	if (other.clockSeqHiAndReserved != this.clockSeqHiAndReserved)
	    return this.clockSeqHiAndReserved - other.clockSeqHiAndReserved;
	if (other.clockSeqLow != this.clockSeqLow)
	    return this.clockSeqLow - other.clockSeqLow;
	for (int i = 0; i < 6; i++)
	{
	    if (other.nodeId[i] != this.nodeId[i])
		return this.nodeId[i] - other.nodeId[i];
	}
	return 0;
    }
	    
    public int hashCode()
    {
	return (int)timeLow;
    }


    private static char hexDigits[] = {
	'0', '1', '2', '3', '4', '5', '6', '7',
	'8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    private static String toHex(byte num)
    {
	return toHex(((int)num & 0xFF), 2);
    }

    private static String toHex(short num)
    {
	return toHex(((int)num & 0xFFFF), 4);
    }

    private static String toHex(int num)
    {
	return toHex(num, 8);
    }

    private static String toHex(int num, int digits)
    {
	String hex = Integer.toHexString(num);
	if (hex.length() < digits)
	{
	    StringBuffer sb = new StringBuffer();
	    int toAdd = digits - hex.length();
	    for (int i = 0; i < toAdd; i++)
	    {
		sb.append('0');
	    }
	    sb.append(hex);
	    hex = new String(sb);
	}
	return hex.toUpperCase();
    }
	    
    private static class UUIDGenerator {
	
	private static UUIDGenerator theGenerator;

	
	private Random random1;
	private Random random2;

	
	private long lastTime;

	private long timeAdjust;

	private int clockSequence;

	private byte nodeId[];

	/** clock sequence 14 bits */
	private static final int CLOCK_SEQ_MASK 	= 0x3FFF;

	/** UUID version is one bit */
	private static final int VERSION_BITS		= 0x1000;

	/** One bit is reserved */
	private static final int RESERVED_BITS		= 0x0080;

	private static final int TIME_ADJUST_MASK	= 0x0FFF;

	private static final long N100NS_PER_MILLI	= 10000;

        // время (милисек) от 15.10.1582 до 01.01.1970        
	private static final long EPOCH_CVT		= 0x1B21DD213814000L;

	public synchronized static UUIDGenerator create()
	{
	    if (theGenerator == null)
		theGenerator = new UUIDGenerator();
	    return theGenerator;
	}

	private UUIDGenerator()
	{
	    random1 = new Random(this.hashCode());
	    random2 = new Random(Thread.currentThread().hashCode());
	    nodeId = new byte[6];
	    initializeNodeId();
	    getClockSequence();
	}

	/** Generate a new UUID. */
	public synchronized void generate(UUID uuid) {
            // возвращает время с 1 января 1970 года
	    long now = System.currentTimeMillis();

	    while (true)
	    {
		if (now > lastTime)
		{
		    /* The easiest case */
		    getTimeAdjust();
		    break;
		}
		else if (now < lastTime)
		{
		    getClockSequence();
		    getTimeAdjust();
		    break;
		}
		else
		{
		    /* регулировка времени, чтобы избежать дублирования. */
		    timeAdjust++;
		    if (timeAdjust < N100NS_PER_MILLI)
		    {
			break;
		    }
		    else
		    {
			Thread.yield();
		    }
		}
	    }

	    lastTime = now;
	    long result = 
		    (lastTime * N100NS_PER_MILLI) + EPOCH_CVT + timeAdjust;

	    uuid.timeLow = (int)(result & 0xFFFFFFFF);
	    uuid.timeMid = (short)((result >> 32) & 0xFFFF);
	    uuid.timeHiAndVersion = 
		    (short)(((result >> 48) & 0xFFFF) | VERSION_BITS);
	    uuid.clockSeqHiAndReserved =
		    (byte)(((clockSequence >> 8) & 0xFF) | RESERVED_BITS);
	    uuid.clockSeqLow = (byte)(clockSequence & 0xFF);
	    for (int i = 0; i < 6; i++)
	    {
		uuid.nodeId[i] = nodeId[i];
	    }
	}

	private void getTimeAdjust() {
	    timeAdjust = random2.nextInt() & TIME_ADJUST_MASK;
	}
		
	/** произвольное время */
	private void getClockSequence()
	{
	    clockSequence = random1.nextInt() & CLOCK_SEQ_MASK;
	    if (clockSequence == 0)
		clockSequence++;
	}
			
		
	/** случайным образом генерировать идентификатор узла. сделать последние два байта 
	* 0xAA77, который не может конфликтовать с реальным адресом Ethernet */
	private void initializeNodeId()
	{
	    byte barr[] = new byte[2];

	    Random r1 = new Random();
	    Random r2 = new Random(r1.hashCode());
	    r1.nextBytes(barr);
	    nodeId[0] = barr[0];
	    nodeId[1] = barr[1];

	    r2.nextBytes(barr);
	    nodeId[2] = barr[0];
	    nodeId[3] = barr[1];

	    nodeId[4] = (byte)0xaa;
	    nodeId[5] = 0x77;
	}
    }
}
