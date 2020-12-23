package tonfa;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Vector;

/*
X X-a->0 X-b->1
Y Y-a->0 Y-b->1
0 0-a->0 0-b->2
1 1-a->0 1-b->1
2 2-a->0 2-b->Y
*/

public class DFASimp {
	public static String strstr;
	public static String result =new String();
    int start;
    int anum = 0;
    static int letternum = 1;
    static int statuenum = 1;
    static ArrayList letterList;
    //ArrayList changeList;
    static String[][] stateChange;
    static ArrayList statue;
    //char[] endStatue;


    public DFASimp() throws IOException {
        String DFAstr = DFABuildClass.gennarateDFA(strstr);
        String a[] = new String[15];
        statue = new ArrayList();
        letterList = new ArrayList();
        stateChange = new String[20][20];
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                stateChange[i][j] = "z";
            }
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(DFAstr.getBytes(Charset.forName("utf8"))), Charset.forName("utf8")));
        //Scanner sc = new Scanner(System.in);
        String str = br.readLine();
        while (str != null) {
            a[anum] = str;
            anum++;
//            System.out.println(str);
            str = br.readLine();
        }


        for (int i = 0; i < anum; i++) {
            String[] strArr = a[i].split(" ");
            statueadd(strArr[0]);
            for (int t = 1; t < strArr.length; t++) {
                letterChange(strArr[t]);
            }
        }


        for (int i = 0; i < anum; i++) {
            String[] strArr = a[i].split(" ");
            for (int t = 1; t < strArr.length; t++) {
                stateChange(strArr[t]);
            }
        }
//        System.out.println("没锟斤拷锟斤拷之前锟斤拷状态转锟狡憋拷");
//        output();
        DFAsimp();
//        System.out.println("锟斤拷锟斤拷之锟斤拷锟阶刺拷票锟�");
//        output();
        Outputresult();
    }

    public static void letterChange(String c) {
        String[] strArr = c.split("");
        letterListadd(strArr[2]);
    }

    public static void stateChange(String c) {
        int row = 0;
        int col = 0;
        String[] strArr = c.split("");
        row = returnrow(strArr[0]);
        col = returncol(strArr[2]);
        stateChange[row][col] = strArr[5];
    }

    public static void statueadd(String c) {
        if (statue.contains(c)) {
            return;
        }
        statue.add(c);
        stateChange[statuenum][0] = c;
        statuenum++;

    }

    public static void letterListadd(String c) {
        if (letterList.contains(c)) {
            return;
        }
        letterList.add(c);
        stateChange[0][letternum] = c;
        letternum++;
    }

    /*public static void OutputArrayList(ArrayList<ArrayList<String>> arr)
    {
        for(int i = 0; i < arr.size(); i++)
        {
            for(int j = 0; j < arr.get(i).size(); j++)
            {
                System.out.print(arr.get(i).get(j) + " ");
            }
            System.out.println();
        }
    }*/
    public static void DFAsimp() {
        ArrayList<ArrayList<String>> SUMList = new ArrayList<ArrayList<String>>();
        ArrayList<String> ListY = new ArrayList<String>();
        ListY.add("Y");
        ArrayList<String> Listz = new ArrayList<String>();
        Listz.add("z");
        ArrayList<String> List = new ArrayList<String>(statue);
        List.remove(1);
        SUMList.add(List);
        SUMList.add(ListY);
        SUMList.add(Listz);
		/*String[][] strArr = new String[statue.size()][];
		strArr[0] = new String[statue.size()];
		strArr[0][0] = new String();*/
        boolean flag = true;
        while (flag) {
            //OutputArrayList(NotEndList);
            flag = false;
            for (int i = 0; i < SUMList.size(); i++) {
                List = SUMList.get(i);
                if (List.size() == 1) {
                    continue;
                }
                for (int j = 0; j < letternum - 1; j++) {
                    int size = List.size();
                    int[] index = new int[size];
                    for (int k = 0; k < size; k++) {
                        String row = List.get(k);
                        String col = (String) letterList.get(j);
                        String nextHop = stateChange[returnrow(row)][returncol(col)];
                        for (int l = 0; l < SUMList.size(); l++) {
                            if (hasElem(SUMList.get(l), nextHop)) {
                                index[k] = l;
                                break;
                            }
                        }
                    }

                    ArrayList<ArrayList<Integer>> divideIndex = divide(index);
                    if (divideIndex.size() == 1) {
                        continue;
                    }
                    SUMList.remove(i);
                    for (int l = 0; l < divideIndex.size(); l++) {
                        ArrayList<String> elem = new ArrayList<String>();
                        for (int m = 0; m < divideIndex.get(l).size(); m++) {
                            elem.add(List.get(divideIndex.get(l).get(m)));
                        }
                        SUMList.add(l, elem);
                    }
                    flag = true;
                }
            }
        }


        for (int i = 0; i < SUMList.size(); i++) {
            if (SUMList.get(i).size() > 1) {
                String str;
                str = SUMList.get(i).get(0);

                for (int j = 1; j < SUMList.get(i).size(); j++) {
                    String s = SUMList.get(i).get(j);
                    int row = returnrow(s);
                    stateChange[row][0] = "z";
                    for (int k = 1; k < statuenum; k++) {
                        for (int l = 1; l < letternum; l++) {
                            if (stateChange[k][l].equals(s)) {
                                stateChange[k][l] = str;
                            }
                        }
                    }
                }
            }
        }


    }

    public static int returnrow(String c) {
        int row = 0;
        for (int i = 1; i < statuenum; i++) {
            if (c.equals(stateChange[i][0]))
                row = i;
        }
        return row;

    }

    public static int returncol(String c) {
        int col = 0;
        for (int j = 1; j < letternum; j++) {
            if (c.equals(stateChange[0][j]))
                col = j;
        }
        return col;
    }

    public static boolean hasElem(ArrayList<String> arr, String elem) {
        for (int i = 0; i < arr.size(); i++) {
            if (arr.get(i).equals(elem)) {
                return true;
            }
        }
        return false;
    }

    public static ArrayList<ArrayList<Integer>> divide(int[] a) {
        ArrayList<ArrayList<Integer>> arr = new ArrayList<ArrayList<Integer>>();
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(a[0]);
        list.add(0);
        arr.add(list);
        boolean flag;
        for (int i = 1; i < a.length; i++) {
            flag = false;
            for (int j = 0; j < arr.size(); j++) {
                if (a[i] == arr.get(j).get(0)) {
                    flag = true;
                    arr.get(j).add(i);
                    break;
                }
            }
            if (flag == false) {
                list = new ArrayList<Integer>();
                list.add(a[i]);
                list.add(i);
                arr.add(list);
            }
        }
        for (int j = 0; j < arr.size(); j++) {
            arr.get(j).remove(0);
        }
        return arr;
    }

    public static void output() {
        for (int i = 0; i < statuenum; i++) {
            for (int j = 0; j < letternum; j++) {
                System.out.print(stateChange[i][j] + "  ");
            }
            System.out.println();
        }
    }

    public static String Outputresult() {
    	//System.out.println(letternum);
    	//System.out.println(statuenum);
        int x=1;
    	for(x=1;x<letternum-1;x++)
    	{
    		System.out.print(stateChange[0][x]+" ");
    		result=result+stateChange[0][x]+" ";
    	}
    	System.out.print(stateChange[0][x]+"#\n");
    	result=result+stateChange[0][x];
    	result=result+"#\n";
    	
    	int y=1;
    	for(y=1;y<statuenum-1;y++)
    	{
    		if (stateChange[y][0].equals("z"))
    			continue;
		    result=result+stateChange[y][0]+" ";
		    System.out.print(stateChange[y][0]+" ");
    	}
    	if (!stateChange[y][0].equals("z"))
    	{
    	System.out.print(stateChange[y][0]+"#\n");
    	result=result+stateChange[y][0];
    	result=result+"#\n";
    	}
    	if(stateChange[y][0].equals("z"))
    	{
    		System.out.print("#\n");
    		result=result+"#\n";
    	}
    		
    	
        for (int i = 1; i < statuenum; i++) {
            if (stateChange[i][0].equals("z"))
                continue;
            System.out.print(stateChange[i][0]);
            result=result+stateChange[i][0];
            for (int j = 1; j < letternum; j++) {
                if (stateChange[i][j].equals("z"))
                    continue;
                System.out.print(" " + stateChange[i][0] + "-" + stateChange[0][j] + "->" + stateChange[i][j]);
                result=result+" " + stateChange[i][0] + "-" + stateChange[0][j] + "->" + stateChange[i][j];
            }
            System.out.println();
            result=result+"\n";
        }
        return result;

    }
    
    public static String getResult()
    {
    	return result;
    }

    public static void main(String[] args) throws IOException {
        new DFASimp();
    }
}
