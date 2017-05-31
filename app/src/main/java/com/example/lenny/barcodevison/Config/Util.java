package com.example.lenny.barcodevison.Config;

/**
 * Created by lenny on 5/31/17.
 */

public class Util {


    static String convert_upce(String value) {
//        UPC-E code ends in 0, 1, or 2: The UPC-A code is determined by taking the first two digits of the UPC-E code, taking the last digit of the UPC-E code, adding four 0 digits, and then adding characters 3 through 5 from the UPC-E code.
//        UPC-E code ends in 3: The UPC-A code is determined by taking the first three digits of the UPC-E code, adding five 0 digits, then adding characters 4 and 5 from the UPC-E code.
//        UPC-E code ends in 4: The UPC-A code is determined by taking the first four digits of the UPC-E code, adding five 0 digits, then adding the fifth character from the UPC-E code.
//        UPC-E code ends in 5, 6, 7, 8, or 9: The UPC-A code is determined by taking the first give digits of the UPC-E code, adding four 0 digits, then adding the last character from the UPC-E code.
//
        String manufacturer_code, product_code;
        char lastdigit = (value.charAt(value.length() - 1));
        String last = String.valueOf(lastdigit);
        switch (lastdigit) {
            case '0':
            case '1':
            case '2':
                manufacturer_code = value.substring(0, 2) + last + "00";
                product_code = "00" + value.substring(2, 5);
                break;
            case '3':
                manufacturer_code = value.substring(0, 3) + "00";
                product_code = "000" + value.substring(3, 5);
                break;
            case '4':
                manufacturer_code = value.substring(0, 4) + "0";
                product_code = "0000" + value.substring(4, 5);
                break;
            default:
                manufacturer_code = value.substring(0, 5);
                product_code = "0000" + last;
                break;
        }
        return ("0" + manufacturer_code + product_code);
    }
}

