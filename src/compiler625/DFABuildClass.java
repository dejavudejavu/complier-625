package compiler625;


/*import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Scanner;

public class DFABuildClass {
    public static NFA nfa;
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
        int i,j;
        for (i = 0; i < node.pathChar.size(); i++) {
            if (node.pathChar.get(i).equals(c)) {
                NFANode temp = node.nextNodes.get(i);
                if (!resultList.contains(temp))
                    resultList.add(temp);
                //加入后置节点
                getAllNode(temp, resultList, c);
                //加入前驱节点
                addctoNode(temp,resultList,"~");
            }
        }
    }

    static void move(NFANode node, ArrayList<NFANode> resultList, String c) {
        int i,j;
        for (i = 0; i < node.pathChar.size(); i++) {
            if (node.pathChar.get(i).equals(c)) {
                NFANode temp = node.nextNodes.get(i);
                if (!resultList.contains(temp))
                    resultList.add(temp);
                //加入后置节点
                getAllNode(temp, resultList, "~");
                //加入前驱节点
                addctoNode(temp,resultList, "~");
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
    
    //加入该节点是否还是通过符号c和其余的节点连接的节点
    static void addctoNode(NFANode node, ArrayList<NFANode> resultList, String c)
    {
    	int i,n;
    	NFANode tempnode;
    	for(i=0;i<nfa.nodeList.size();i++)
    	{
    		tempnode = nfa.nodeList.get(i);
	    	n = tempnode.nextNodes.indexOf(node);
	    	if(n!=-1&&tempnode.pathChar.get(n).equals(c)&&!resultList.contains(tempnode))
	    	{
	    		resultList.add(tempnode);
	    		addctoNode(tempnode,resultList, "~");
	    	}
    	}
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

    static String gennarateDFA(String strstr) throws IOException {
    	String NFAstr = FAtoNFA.genarateNFA(strstr);
    	System.out.println(NFAstr);
    	BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(NFAstr.getBytes(Charset.forName("utf8"))), Charset.forName("utf8")));
        //2020.12.5更改   没有注意中间符号的问题
        //加上中间符号数组
        ArrayList<String> path_chars = new ArrayList<>();
        //将NFA分解
        nfa=new NFA();
//        Scanner sc = new Scanner(System.in);
        NFANode node;
        String path_char;
        // 2020.12.9更改  尝试bufferedReader
//        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String str;
        while ((str = br.readLine()) != null) {
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
            try {
                if (str.length() >= 6) {
                    for (int i = 1; i < strArr.length; i++) {
//                    node.pathChar.add(strArr[i].charAt(2));   这里由于符号不一定为一个字符，所以进行更改
                        path_char = strArr[i].split("-")[1];
                        node.pathChar.add(path_char);
                        //建立好中间符号集
                        //去掉~
                        if (!path_char.equals("~") && !path_chars.contains(path_char)) {
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
            } catch (Exception e) {
                System.out.println(str);
            }
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
            for (String path_character : path_chars) {
                tempList2 = new ArrayList<>();
                for (i = 0; i < tempList1.size(); i++) {
                    move(tempList1.get(i), tempList2, path_character);
                }
                if (tempList2.size() == 0) {
                    List.add(-1);
                    continue;
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
        String result;
        String c;
        int total = dfa.dfaStateList.size();
        c = dfa.dfaStateList.get(0);
//        System.out.print(c);
        result=c;
        for (int m = 0; m < dfa.sigma.size(); m++) {
            if(dfa.tansList.get(0).get(m) != -1){
//                System.out.print(" " + c + "-" + dfa.sigma.get(m) + "-" + dfa.dfaStateList.get(dfa.tansList.get(0).get(m)));
            	result+=" " + c + "-" + dfa.sigma.get(m) + "->" + dfa.dfaStateList.get(dfa.tansList.get(0).get(m));
            }
        }
//        System.out.println("");
        result+="\n";
        c = dfa.dfaStateList.get(total - 1);
//        System.out.print(c);
        result+=c;
        for (int m = 0; m < dfa.sigma.size(); m++) {
            try{
//                System.out.print(" " + c + "-" + dfa.sigma.get(m) + "-" + dfa.dfaStateList.get(dfa.tansList.get(total - 1).get(m)));
            	result+=" " + c + "-" + dfa.sigma.get(m) + "->" + dfa.dfaStateList.get(dfa.tansList.get(total - 1).get(m));
            }
            catch (Exception ignored){}
        }
//        System.out.println("");
        result += "\n";
        for (i = 1; i < total - 1; i++) {
            c = dfa.dfaStateList.get(i);
//            System.out.print(c);
            result+=c;
            for (int m = 0; m < dfa.sigma.size(); m++) {
                try{
//                    System.out.print(" " + c + "-" + dfa.sigma.get(m) + "-" + dfa.dfaStateList.get(dfa.tansList.get(i).get(m)));
                	result+=" " + c + "-" + dfa.sigma.get(m) + "->" + dfa.dfaStateList.get(dfa.tansList.get(i).get(m));
                }
                catch (Exception ignored){}
            }
//            System.out.println("");
            result+="\n";
        }
        System.out.print(result);
        return result;
    }
}*/
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
        public ArrayList<NFANode> beforeNodes;  //和这个结点用~连接的符号

        NFANode(String NodeName) {
            this.NodeName = NodeName;
            pathChar = new ArrayList<>();
            nextNodes = new ArrayList<>();
            beforeNodes = new ArrayList<>();
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
        int i,j;
        for (i = 0; i < node.pathChar.size(); i++) {
            if (node.pathChar.get(i).equals(c)) {
                NFANode temp = node.nextNodes.get(i);
                if (!resultList.contains(temp))
                    resultList.add(temp);
                //加入后置节点
                getAllNode(temp, resultList, c);
                //加入前驱节点
                addctoNode(temp,resultList);
            }
        }
    }

    //判断node结点的move集，并加入到resultList中
    static void move(NFANode node, ArrayList<NFANode> resultList, String c) {
        int i,j;
        for (i = 0; i < node.pathChar.size(); i++) {
            if (node.pathChar.get(i).equals(c)) {
                NFANode temp = node.nextNodes.get(i);
                if (!resultList.contains(temp))
                    resultList.add(temp);
                //加入后置节点
                getAllNode(temp, resultList, "~");
                //加入前驱节点
                addctoNode(temp,resultList);
            }
        }

    }

    //判断是否两个函数相等
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

    //12.17 由于重构数据结构，函数无效，所以修改函数
    //加入该节点是否还是通过符号c和其余的节点连接的节点
    static void addctoNode(NFANode node, ArrayList<NFANode> resultList)
    {
        int i;
        for(i=0;i<node.beforeNodes.size();i++)
        {
            NFANode tempNode = node.beforeNodes.get(i);
            if(!resultList.contains(tempNode))
            {
                resultList.add(tempNode);
                addctoNode(tempNode, resultList);
            }
        }
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

    static String gennarateDFA(String strstr) throws IOException {
    	String NFAstr = FAtoNFA.genarateNFA(strstr);
    	System.out.println(NFAstr);
    	BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(NFAstr.getBytes(Charset.forName("utf8"))), Charset.forName("utf8")));
//        String NFAstr = FAtoNFA.genarateNFA(strstr);
//        BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(NFAstr.getBytes(Charset.forName("utf8"))), Charset.forName("utf8")));
        //2020.12.5更改   没有注意中间符号的问题
        //加上中间符号数组
        ArrayList<String> path_chars = new ArrayList<>();
        //将NFA分解
        NFA nfa=new NFA();
//        Scanner sc = new Scanner(System.in);
        NFANode node;
        String path_char;
        // 2020.12.9更改  尝试bufferedReader
       // BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String str;
        while ((str = br.readLine()) != null) {
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
            try {
                if (str.length() >= 6) {
                    for (int i = 1; i < strArr.length; i++) {
//                    node.pathChar.add(strArr[i].charAt(2));   这里由于符号不一定为一个字符，所以进行更改
                        path_char = strArr[i].split("-")[1];
                        node.pathChar.add(path_char);
                        //建立好中间符号集
                        //去掉~
                        if (!path_char.equals("~") && !path_chars.contains(path_char)) {
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
                        if(path_char.equals("~"))
                        {
                            nfa.nodeList.get(temp).beforeNodes.add(node);
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println(str);
            }
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
            for (String path_character : path_chars) {
                tempList2 = new ArrayList<>();
                for (i = 0; i < tempList1.size(); i++) {
                    move(tempList1.get(i), tempList2, path_character);
                }
                if (tempList2.size() == 0) {
                    List.add(-1);
                    continue;
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
        int end=0;    //第end个状态为终态
        boolean haveend=false;  //终态是否判断出来
        for (i = 1; i < totalList.size(); i++) {
            if(!haveend) {
                for (j = 0; j < totalList.get(i).size(); j++) {
                    if (totalList.get(i).get(j).NodeName.equals("Y")) {
                        dfa.dfaStateList.add("Y");
                        end = i;
                        haveend = true;
                        break;
                    }
                }
                if (j == totalList.get(i).size())
                    dfa.dfaStateList.add(Integer.toString(i-1));
            }
            else
                dfa.dfaStateList.add(Integer.toString(i-1));
        }
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
//        for(i=0;i<dfa.dfaStateList.size();i++)
//        {
//            System.out.print(dfa.dfaStateList.get(i));
//        }
        //将DFA输出
        String result;
        String c;
        int total = dfa.dfaStateList.size();
        c = dfa.dfaStateList.get(0);
//        System.out.print(c);
        result=c;
        for (int m = 0; m < dfa.sigma.size(); m++) {
            if(dfa.tansList.get(0).get(m) != -1){
//                System.out.print(" " + c + "-" + dfa.sigma.get(m) + "->" + dfa.dfaStateList.get(dfa.tansList.get(0).get(m)));
                result+=" " + c + "-" + dfa.sigma.get(m) + "->" + dfa.dfaStateList.get(dfa.tansList.get(0).get(m));
            }
        }
//        System.out.println("");
        result+="\n";
        c = dfa.dfaStateList.get(end);
//        System.out.print(c);
        result+=c;
        for (int m = 0; m < dfa.sigma.size(); m++) {
            try{
//                System.out.print(" " + c + "-" + dfa.sigma.get(m) + "->" + dfa.dfaStateList.get(dfa.tansList.get(total - 1).get(m)));
                result+=" " + c + "-" + dfa.sigma.get(m) + "->" + dfa.dfaStateList.get(dfa.tansList.get(end).get(m));
            }
            catch (Exception ignored){}
        }
//        System.out.println("");
        result += "\n";
        for (i = 1; i < total; i++) {
            if(i==end)  //终态不重复输出
                continue;
            c = dfa.dfaStateList.get(i);
//            System.out.print(c);
            result+=c;
            for (int m = 0; m < dfa.sigma.size(); m++) {
                try{
//                    System.out.print(" " + c + "-" + dfa.sigma.get(m) + "->" + dfa.dfaStateList.get(dfa.tansList.get(i).get(m)));
                    result+=" " + c + "-" + dfa.sigma.get(m) + "->" + dfa.dfaStateList.get(dfa.tansList.get(i).get(m));
                }
                catch (Exception ignored){}
            }
//            System.out.println("");
            result+="\n";
        }
        System.out.print(result);
        return result;
    }
    /*public static void main(String[] args) throws IOException {
        gennarateDFA();
    }*/
}








