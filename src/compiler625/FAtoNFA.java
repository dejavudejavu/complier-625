package compiler625;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;

import javax.swing.Spring;
import javax.swing.text.Document;

import com.sun.net.httpserver.Authenticator.Result;





public class FAtoNFA {
	
	public SymbolStack caozuofuStack;
	public NFAStack nfaStack;
	int reLength;
	public int num=-1;
	
	//状态锟节碉拷
	public class NFANode{
	      public String stateNum;//锟斤拷前状态锟节碉拷锟街�
	      public char pathChar;//前一状态锟斤拷锟斤拷前状态锟斤拷锟斤拷锟街凤拷
	      public List<NFANode> nextNodes;//锟斤拷探诘慵拷锟�
	      NFANode(String stateNum,char pathChar) {
	    	  this.stateNum=stateNum;
	    	  this.pathChar=pathChar;
	    	  nextNodes = new LinkedList<>();
	      }
	  }
	//NFA图
	public class NFA{
	      public NFANode headNode;
	      public NFANode tailNode;
	      NFA(){
	    	  
	      }
	      NFA(NFANode headNode,NFANode tailNode){
	    	  this.headNode=headNode;
	    	  this.tailNode=tailNode;
	      }   
	  }
	//锟斤拷锟斤拷栈
	public class SymbolNode {	
	    public char currentSymbol;	
	    public SymbolNode nextSymbol;
	    public SymbolNode() {
	    	currentSymbol=0;
		}
	}
	public class SymbolStack{
		SymbolNode top=new SymbolNode();
	    public SymbolStack() {	 
	    }
	    public SymbolStack(char currentSymbol) {
	    	top.currentSymbol=currentSymbol;
	    	top.nextSymbol=null;
		}	
	    public char Pop() {
	    	char current=top.currentSymbol;
	    	top=top.nextSymbol;
	    	return current;
		}
	    public void Push(char newSymbol) {
	    	SymbolNode oldTop=top;
	    	top=new SymbolNode();
	    	top.currentSymbol=newSymbol;
	    	top.nextSymbol=oldTop;
	    	oldTop=null;
		}	
	    public char Peek() {
			return top.currentSymbol;
		}
	}
	//NFA栈
	public class Node {
	    public NFA currentNFA;
	    public Node nextNFA;
	    Node(){
	    	currentNFA=new NFA();
	    }
	}
	public class NFAStack {
		Node top=new Node();
		public NFAStack() {
			
		}
		public NFAStack(NFA currentNFA) {
			top.currentNFA=currentNFA;
			top.nextNFA=null;
		}
		public NFA Pop() {
			NFA current=top.currentNFA;
			top=top.nextNFA;
			return current;
		}
		public void Push(NFA newNFA) {
			Node oldTop=top;
			top=new Node();
			top.currentNFA=newNFA;
			top.nextNFA=oldTop;
			oldTop=null;
		}
		public NFA Peek() {
			return top.currentNFA;
		}
	}

	public class GetStateNumber {
		public String getStateNum() {
			++num;
			return String.valueOf(num);
		}
	}
	public class GenerateNFAMethod{
	    GetStateNumber getNum = new GetStateNumber();
	    public char nul = '~';
		public NFA meetNonSymbol(char nonSymbol)
		{
			NFANode headNode = new NFANode(getNum.getStateNum(), nul);
			NFANode tailNode = new NFANode(getNum.getStateNum(), nonSymbol);
			headNode.nextNodes.add(tailNode);
			NFA newNFA = new NFA(headNode, tailNode);
			return newNFA;
		}
		public NFA meetStarSymbol(NFA oldNFA)
		{
			NFANode oldHeadNode = oldNFA.headNode;
			NFANode oldTailNode = oldNFA.tailNode;
			NFANode newHeadNode = new NFANode(getNum.getStateNum(), nul);
			NFANode newTailNode = new NFANode(getNum.getStateNum(), nul);
			newHeadNode.nextNodes.add(oldHeadNode);
			newHeadNode.nextNodes.add(newTailNode);
			oldTailNode.nextNodes.add(newTailNode);
			oldTailNode.nextNodes.add(oldHeadNode);
			NFA newNFA = new NFA(newHeadNode, newTailNode);
			return newNFA;
		}
		public NFA meetAndSymbol(NFA firstNFA, NFA secondNFA) {
			NFANode newHeadNode = new NFANode(getNum.getStateNum(), nul);
			NFANode newTailNode = new NFANode(getNum.getStateNum(), nul);
			firstNFA.tailNode.nextNodes.add(secondNFA.headNode);
			newHeadNode.nextNodes.add(firstNFA.headNode);
			secondNFA.tailNode.nextNodes.add(newTailNode);
			NFA newNFA = new NFA(newHeadNode, newTailNode);
			return newNFA;
		}
		

		public NFA meetOrSymbol(NFA firstNFA, NFA secondNFA) {
			NFANode oldFirstHeadNode = firstNFA.headNode;
			NFANode oldSecondHeadNode = secondNFA.headNode;
			NFANode oldFirstTailNode = firstNFA.tailNode;
			NFANode oldSecondTailNode = secondNFA.tailNode;
			NFANode newHeadNode = new NFANode(getNum.getStateNum(), nul);
			NFANode newTailNode = new NFANode(getNum.getStateNum(), nul);
			newHeadNode.nextNodes.add(oldFirstHeadNode);
			newHeadNode.nextNodes.add(oldSecondHeadNode);
			oldFirstTailNode.nextNodes.add(newTailNode);
			oldSecondTailNode.nextNodes.add(newTailNode);
			NFA newNFA = new NFA(newHeadNode, newTailNode);
			return newNFA;
		}
	}
	public NFA Transfer(String suffix) {
		int i=0;
		char ch=suffix.charAt(i);
		while(ch!='#')
		{
			if (IfCaozuofu(ch)) {
				switch (ch) {
				case '*':
					NFA top = nfaStack.Pop();
					if (top == null)
						return null;
					NFA nfa = generator.meetStarSymbol(top);
					nfaStack.Push(nfa);
					break;
				case '|':
					NFA secondNFA = nfaStack.Pop();
					NFA firstNFA = nfaStack.Pop();
					NFA newOrNFA = generator.meetOrSymbol(firstNFA, secondNFA);
					nfaStack.Push(newOrNFA);
					break;
				case '~':
					NFA secondNFA1 = nfaStack.Pop();
					NFA firstNFA1 = nfaStack.Pop();
					NFA newAndNFA = generator.meetAndSymbol(firstNFA1, secondNFA1);
					nfaStack.Push(newAndNFA);
					break;
				}
			} else if (IfCaozuoshu(ch)) {
				NFA nfa = generator.meetNonSymbol(ch);
				nfaStack.Push(nfa);
			}		
			ch=suffix.charAt(++i);
		}

		return nfaStack.Pop();
	}

	private boolean IfCaozuoshu(char c) {
		byte b = (byte) c;
        if ((b >= 97 && b <= 122) || (b >= 65 && b <= 90)||(b>=48 &&b<=57)) {
        	return true;
        }
		return false;
	}

	private boolean IfCaozuofu(char c) {
		if("~*|".contains(c+"")) {
			return true;
		}
		return false;
	}

	public static int currentIndex = -1;


	public GenerateNFAMethod generator = new GenerateNFAMethod();

	public class inputException extends Exception {
	    public inputException(String msg)
	    {
	        super(msg);
	    }
	}

	public String Init(String re) throws Exception{
		String result=new String();
		if (re.length()==0)
			throw new inputException("杈撳叆涓嶈兘涓虹┖");		
		for (int i = 0; i < re.length(); i++) {
			char c=re.charAt(i);
			if(IfCaozuofu(c)||IfCaozuoshu(c)) {
			}
			else if(c=='('||c==')') {
				
			}
			else
				throw new inputException("杈撳叆瀛楃涓嶇鍚堣姹�");	
		}

		StringBuffer sb = new StringBuffer(); 
		sb.append(re);
		for(int i = 0; i < sb.length()-1; i++) {
			char c=sb.charAt(i);
			
			char cnext=sb.charAt(i+1);
			if(IfCaozuoshu(c)) {
				if(cnext=='('||IfCaozuoshu(cnext)){
					sb.insert(i+1,"~");
				}
			}
			else if(c=='*') {
				if(cnext=='|'||cnext==')') {
					
				}
				else {
					sb.insert(i+1,"~");
				}
			}
			else if(c==')') {
				if(cnext=='(') {
					sb.insert(i+1,"~");
				}
			}
		}
		re =sb.toString();
		String suffix=toSuffix(re);

		suffix+="#";

		nfaStack=new NFAStack();
		result=CreateNFAGraphic(Transfer(suffix));

		return result;
	}

	public String toSuffix(String re) throws Exception {
		SymbolStack transStack =new SymbolStack();
		String result=new String();
		for(int i=0;i<re.length();i++) {
			char current=re.charAt(i);
			char top=transStack.top.currentSymbol;
			if(IfCaozuofu(current)) {
				if(top==0||top=='('||priority(current)>priority(top)) {
					transStack.Push(current);
				}
				else {
					while(transStack.top.currentSymbol!=0&&transStack.top.currentSymbol!='(') {
						if(priority(current)<=priority(transStack.top.currentSymbol)) {
							result+=""+transStack.Pop();
						}
					}
					transStack.Push(current);
				}
			}
			else if(IfCaozuoshu(current)) {
				result+=""+current;
			}
			else if(current=='(') {
				transStack.Push(current);
			}
			else if(current==')') {
				while(transStack.top.currentSymbol!=0) {
					if(transStack.top.currentSymbol=='(') {
						transStack.Pop();
						break;
					}
					else {
						result+=""+transStack.Pop();
					}
				}
			}
		}
		while(transStack.top.currentSymbol!=0) {
			result+=""+transStack.Pop();
		}
		return result;
	}
	private int priority(char current) {
		if(current=='*') {
			return 2;
		}
		else if (current == '~') {
			return 1;
		}
		else if (current == '|') {
			return 0;
		}
		return -1;
	}

	public String CreateNFAGraphic(NFA nfa){
		String result=new String();
		LinkedList<String> numList=new LinkedList<String>();
		Queue<NFANode> nfaNodeQueue = new LinkedList<NFANode>();
		LinkedList<NFANode> tags = new LinkedList<NFANode>();
		nfa.headNode.stateNum="X";
		nfa.tailNode.stateNum="Y";
//		System.out.print("X"+" ");
		result=result.concat("X"+" ");
		for (int i = 0; i < nfa.headNode.nextNodes.size(); i++) {
//			System.out.print(nfa.headNode.stateNum+"-"+nfa.headNode.nextNodes.get(i).pathChar+"->"+nfa.headNode.nextNodes.get(i).stateNum+" ");
			result=result.concat(nfa.headNode.stateNum+"-"+nfa.headNode.nextNodes.get(i).pathChar+"->"+nfa.headNode.nextNodes.get(i).stateNum+" ");
			nfaNodeQueue.add(nfa.headNode.nextNodes.get(i));
		}
//		System.out.println();
//		System.out.print("Y"+" ");
		result=result.concat("\n"+"Y"+" ");
		for (int i = 0; i < nfa.tailNode.nextNodes.size(); i++) {
//			System.out.print(nfa.tailNode.stateNum+"-"+nfa.tailNode.nextNodes.get(i).pathChar+"->"+nfa.tailNode.nextNodes.get(i).stateNum+" ");
			result=result.concat(nfa.tailNode.stateNum+"-"+nfa.tailNode.nextNodes.get(i).pathChar+"->"+nfa.tailNode.nextNodes.get(i).stateNum+" ");
		}

		numList.add(nfa.headNode.stateNum);
		numList.add(nfa.tailNode.stateNum);
		while (nfaNodeQueue.size()!= 0) {
			NFANode node = nfaNodeQueue.poll();
			if (!numList.contains(node.stateNum)) {
//				System.out.print("\n"+node.stateNum+" ");
				result=result.concat("\n"+node.stateNum+" ");
				if (node != null && node.nextNodes != null) {
					for (int i = 0; i < node.nextNodes.size(); i++) {
						if (node.nextNodes.get(i) != null) {
//							System.out.print(node.stateNum+"-"+node.nextNodes.get(i).pathChar+"->"+node.nextNodes.get(i).stateNum+" ");
							result=result.concat(node.stateNum+"-"+node.nextNodes.get(i).pathChar+"->"+node.nextNodes.get(i).stateNum+" ");
						}
						nfaNodeQueue.add(node.nextNodes.get(i));
					}
					numList.add(node.stateNum);
				}
			}
		}
		return result;
    }

	
	
	public static String genarateNFA(String str) {
		FAtoNFA tonfa= new FAtoNFA();
		String result = null;
		try {
			result=tonfa.Init(str);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			return result;
		}
	}
//	public static void main(String args[]) {
//		FAtoNFA tonfa= new FAtoNFA();
//		Scanner input=new Scanner(System.in); 
//		String str=input.next();
//		try {
//			System.out.print(tonfa.Init(str));
//		} catch (Exception e) {
//			System.out.println("杈撳叆涓嶆纭�");
//			e.printStackTrace();
//		}		
//	}
}
