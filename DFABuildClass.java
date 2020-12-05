package tonfa;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class DFABuildClass {
    public static class NFA {
        public String[] sigma;
        public ArrayList<NFANode> nodeList = new ArrayList<>();

        NFA() {
        }

        public int find(String str) {
            for (int i = 0; i < nodeList.size(); i++) {
                if (str.equals(nodeList.get(i).NodeName))
                    return i;
            }
            return -1;
        }
    }

    public static class NFANode {
        public String NodeName;
        public ArrayList<String> pathChar;
        public ArrayList<NFANode> nextNodes;

        NFANode(String NodeName) {
            this.NodeName = NodeName;
            pathChar = new ArrayList<>();
            nextNodes = new ArrayList<>();
        }
    }

    public static class DFA {
        public ArrayList<String> dfaStateList = new ArrayList<>();
        public ArrayList<String> sigma = new ArrayList<>();
        public ArrayList<ArrayList<Integer>> tansList;

        DFA() {
            tansList = new ArrayList<>();
        }
    }

    static void getAllNode(NFANode node, ArrayList<NFANode> resultList, String c) {
//        int i = node.pathChar.indexOf(c);
        //由于一个节点可以经过相同的符号到不一样的状态，所以不能只取第一个
        int i;
        for(i=0;i<node.pathChar.size();i++) {
            if (node.pathChar.get(i).equals(c)) {
                NFANode temp = node.nextNodes.get(i);
                if (!resultList.contains(temp))
                    resultList.add(temp);
                getAllNode(temp, resultList, c);
            }
        }
    }

    static void move(NFANode node, ArrayList<NFANode> resultList, String c) {
        int i;
        for(i=0;i<node.pathChar.size();i++) {
            if (node.pathChar.get(i).equals(c)) {
                NFANode temp = node.nextNodes.get(i);
                if (!resultList.contains(temp))
                    resultList.add(temp);
                getAllNode(temp, resultList, "~");
            }
        }
    }

    static boolean isSame(ArrayList<NFANode> List1, ArrayList<NFANode> List2) {
        if (List1.size() != List2.size()) {
            return false;
        }
        for (NFANode temp : List1) {
            if (!List2.contains(temp)) {
                return false;
            }
        }
        return true;
    }

    static int findList(ArrayList<NFANode> List, ArrayList<ArrayList<NFANode>> totalList) {
        int i;
        for (i = 0; i < totalList.size(); i++) {
            if (isSame(List, totalList.get(i))) {
                return i;
            }
        }
        return -1;
    }

    public static void main(String[] args) {
    	String NFAstr=FAtoNFA.genarateNFA();
    	BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(NFAstr.getBytes(Charset.forName("utf8"))), Charset.forName("utf8")));
        //将NFA分解
        NFA nfa = new NFA();
        //Scanner sc = new Scanner(System.in);
        NFANode node;
        String str;
        System.out.println(NFAstr);
    	
        //2020.12.5更改   没有注意中间符号的问题
        //加上中间符号数组
        ArrayList<String> path_chars = new ArrayList<>();
        //将NFA分解
        String path_char;
		try {
			while ((str=br.readLine())!= null) {
			    if (str.length() == 0)
			        break;
			    String[] strArr = str.split(" ");
			    node = new NFANode(strArr[0]);
			    int index = nfa.find(node.NodeName);
			    if (index == -1) {
			        index = nfa.nodeList.size();
			        nfa.nodeList.add(node);
			    }
			    node = nfa.nodeList.get(index);
			    if (str.length() >= 4) {
			        for (int i = 1; i < strArr.length; i++) {
//                    node.pathChar.add(strArr[i].charAt(2));   这里由于符号不一定为一个字符，所以进行更改
			            path_char = strArr[i].split("-")[1];
			            node.pathChar.add(path_char);
			            //建立好中间符号集
			            //去掉~
			            if (!path_char.equals("~")&&!path_chars.contains(path_char))
			            {
			                path_chars.add(path_char);
			            }
			            String tempStr = strArr[i].split("-")[2].substring(1);
			            //末状态用分割的最后一个字符，并去掉'>'
			            int temp = nfa.find(tempStr);
			            if (temp == -1) {
			                temp = nfa.nodeList.size();
			                nfa.nodeList.add(new NFANode(tempStr));
			            }
			            node.nextNodes.add(nfa.nodeList.get(temp));
			        }
			    }
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        //将分解好的NFA转换成DFA
        DFA dfa = new DFA();
        ArrayList<ArrayList<NFANode>> totalList = new ArrayList<>();
        ArrayList<NFANode> tempList1 = new ArrayList<>();    //I0
//        ArrayList<NFANode> tempList2;    //Ia
//        ArrayList<NFANode> tempList3;    //Ib
        //由Ia，Ib，改为二维数组，来存放不同字符对应的结点
        ArrayList<NFANode> tempList2;
        //建立I0
        NFANode headNode = nfa.nodeList.get(0);
        tempList1.add(headNode);
        getAllNode(headNode, tempList1, "~");
        totalList.add(tempList1);
        //开始循环
        int one = 0;
        int i;
        int j;
        ArrayList<Integer> List;
        while (true) {
            try {
                tempList1 = totalList.get(one);
            } catch (Exception e) {
                break;
            }
//            //a
//            for (i=0;i<tempList1.size();i++) {
//                move(tempList1.get(i), tempList2, 'a');
//            }
//            j = findList(tempList2, totalList);
//            if (j == -1) {
//                j = totalList.size();
//                totalList.add(tempList2);
//            }
//            List = new ArrayList<>();
//            List.add(j);
//
//            //b
//            for (i=0;i<tempList1.size();i++) {
//                move(tempList1.get(i), tempList3, 'b');
//            }
//            j = findList(tempList3, totalList);
//            if (j == -1) {
//                j = totalList.size();
//                totalList.add(tempList3);
//            }
//            List.add(j);
            //以上只考虑了中间符号只有a,b的情况
            List = new ArrayList<>();
            for(String path_character: path_chars)
            {
                tempList2 = new ArrayList<>();
                for (i = 0; i < tempList1.size(); i++) {
                    move(tempList1.get(i), tempList2, path_character);
                }
                j = findList(tempList2, totalList);
                if (j == -1) {
                    j = totalList.size();
                    totalList.add(tempList2);
                }
                List.add(j);
            }
            dfa.tansList.add(List);
            one++;
        }
        //建立好DFA
        dfa.sigma = path_chars;
        dfa.dfaStateList.add("X");
        for (i = 0; i < totalList.size() - 2; i++) {
            dfa.dfaStateList.add(Integer.toString(i));
        }
        dfa.dfaStateList.add("Y");
//
//        // 调试输出
//        for(i=0;i<totalList.size();i++)
//        {
//            for(j=0;j<totalList.get(i).size();j++)
//            {
//                System.out.print(totalList.get(i).get(j).NodeName);
//            }
//            System.out.println("");
//        }
        //将DFA输出
        String c;
        int total=dfa.dfaStateList.size();
        c = dfa.dfaStateList.get(0);
        System.out.print(c);
        for(int m=0;m<dfa.sigma.size();m++){
                System.out.print( " "+c+ "-"+dfa.sigma.get(m) + "-" + dfa.dfaStateList.get(dfa.tansList.get(0).get(m)));
        }
        System.out.println("");
        c = dfa.dfaStateList.get(total-1);
        System.out.print(c);
        for(int m=0;m<dfa.sigma.size();m++){
            System.out.print( " "+c+ "-"+dfa.sigma.get(m) + "-" + dfa.dfaStateList.get(dfa.tansList.get(total-1).get(m)));
        }
        System.out.println("");
        for (i = 1; i < total-1; i++) {
            c=dfa.dfaStateList.get(i);
            System.out.print(c);
            for(int m=0;m<dfa.sigma.size();m++){
                System.out.print( " "+c+ "-"+dfa.sigma.get(m) + "-" + dfa.dfaStateList.get(dfa.tansList.get(i).get(m)));
            }
            System.out.println("");
        }
    }
}



