package compiler625;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class checker {
	static String res;
	private static String[][] stateMatrix;
	
	public static void outputStateMatrix()
	{
		for(int i = 0; i < stateMatrix[0].length; i++)
		{
			System.out.print(stateMatrix[0][i] + " ");
		}
		System.out.println();
		for(int i = 1; i < stateMatrix.length; i++)
		{
			for(int j = 0; j < stateMatrix[1].length; j++)
			{
				System.out.print(stateMatrix[i][j] + " ");
			}
			System.out.println();
		}
	}
	
	public static void fillStateMatrix(int line, String str)
	{
		int matrixLen = stateMatrix[0].length;
		String[] strArr = str.split(" ");
		stateMatrix[line][matrixLen] = strArr[0];
		for(int i = 1; i < strArr.length; i++)
		{
			int flag;
			String[] strArrIdentifier = strArr[i].split("-");
			for(flag = 0; flag < matrixLen; flag++)
			{
				if(strArrIdentifier[1].equals(stateMatrix[0][flag]))
				{
					break;
				}
			}
			stateMatrix[line][flag] = strArrIdentifier[2].substring(1);
		}
	}
	
	public static void checkWord(String word)
	{
		String status = "X";
		for(int i = 0; i < word.length(); i++)
		{
			String w = word.substring(i, i + 1);
			if(w.equals("#"))
			{
				break;
			}
			int row, col, flag = 0;
			for(col = 0; col < stateMatrix[0].length; col++)
			{
				if(w.equals(stateMatrix[0][col]))
				{
					flag = 1;
					break;
				}
			}
			for(row = 1; row < stateMatrix.length; row++)
			{
				if(status.equals(stateMatrix[row][stateMatrix[0].length]))
				{
					break;
				}
			}
			status = stateMatrix[row][col];
			if(flag == 0 || status == null)
			{
				System.out.println("error");
				return;
			}
			System.out.println(w);
		}
		//System.out.println(status);
		if(status.equals("Y"))
		{
			System.out.println("pass");
		} else {
			System.out.println("error");
		}
	}
	
	public static void main(String[] args) throws IOException
	{
		BufferedReader ar = new BufferedReader(new InputStreamReader(System.in)); 
		DFASimp.strstr = ar.readLine();
		new DFASimp();
		res = DFASimp.getResult();
		BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(res.getBytes(Charset.forName("utf8"))), Charset.forName("utf8")));
		String col = br.readLine();
		String[] colArr = col.substring(0, col.length() - 1).split(" ");
		String row = br.readLine();
		String[] rowArr = row.substring(0, row.length() - 1).split(" ");
		stateMatrix = new String[rowArr.length + 1][];
		stateMatrix[0] = colArr;
		for(int i = 1; i <= rowArr.length; i++)
		{
			String str = br.readLine();
			stateMatrix[i] = new String[colArr.length + 1];
			fillStateMatrix(i, str);
		}
		br.close();
		outputStateMatrix();
		//br.readLine();
		String word = ar.readLine();
		while(!word.equals(""))
		{
			checkWord(word);
			word = ar.readLine();
		}
		
	}
}
