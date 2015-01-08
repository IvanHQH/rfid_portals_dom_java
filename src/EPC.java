import org.apache.commons.lang.StringUtils;

public class EPC {
    private String UPC;
    private String EPC;
    public String getUPC(){return UPC;}
	
	public EPC(String epc)
    {
        //(String UPC = EPCToUPC("48150614141000734314159");
    	//String UPC = EPCToUPC("48150614141000734314159"); 
        //String UPC = EPCToUPC("30342848A80A5A80000007E9");
		EPC = epc;
		UPC = EPCToUPC(epc);
    }

    public static String EPCToUPC(String epc)
    {
    	try{
	        String UPC = "";
	        String EPCAux = HexToBinary(epc);
	        String EPCAux2 = HexToBinary(epc);
	
	        int LengthHeader = 14;
	        long GS1CompanyPrefix;
	        long ItemReference;
	        EPCAux2 = EPCAux.substring(LengthHeader,LengthHeader + 24);//6 Digitos GS1 Company prefix            
	        GS1CompanyPrefix = BinaryToLong(EPCAux2);        
	        UPC = StringUtils.leftPad(Long.toString(GS1CompanyPrefix), 6, "0");             
	        EPCAux2 = EPCAux.substring(LengthHeader+24,LengthHeader+24+ 20);//5 Item reference
	        ItemReference = BinaryToLong(EPCAux2);
	        UPC += StringUtils.leftPad(Long.toString(ItemReference), 5, "0");        
	        UPC += CalculateCheckDigit(UPC); 
	        return UPC;   		
    	}catch(NumberFormatException  ex)
    	{
    		return "";
    	}

    }

    private static String HexToBinary(String hexValue)
    {
        int number;
        String binaryString = "";
        for (char d : hexValue.toCharArray())
        {
        	number = Integer.parseInt(Character.toString(d), 16);
        	binaryString += StringUtils.leftPad(Integer.toBinaryString(number), 4, "0");
        }                       
        return binaryString;
    }
    
    private static long BinaryToLong(String binary)
    {
        long dec = 0;
        int i = 0;
        for (int j = binary.length() - 1; j >= 0;j--)
        {
            if (binary.charAt(j) == '1')
                dec += (long)Math.pow(2, i);
            i++;
        }
        return dec;
    }

    private static String CalculateCheckDigit(String TagID)
    {
        int value;
        boolean uneven = true;
        int sumUneven = 0;
        int sumPair = 0;
        float res;
        int numInt;
        for (char Digit : TagID.toCharArray())
        {        	
            value = Integer.valueOf(Character.toString(Digit), 16);
            if (uneven)
                sumUneven += value * 3;
            else
                sumPair += value;
            uneven = !uneven;
        }
        value = sumUneven + sumPair;
        res = (float)value / 10;
        numInt = (int)res;
        res = res - (float)numInt;
        res = (float)Math.rint(res*10)/10;
        res = 1 - res;
        res = (float)Math.round( res*10);
        numInt = (int)res;
        if (res < 10)
            return Integer.toHexString(numInt);
        else
            return "0";
    }
}
