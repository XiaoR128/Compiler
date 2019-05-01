package lex;

import java.util.HashMap;
import java.util.Map;


import javax.swing.table.DefaultTableModel;

public class Analysis {
	private String words;
	
	private static DefaultTableModel tokenModel;
	private static DefaultTableModel charModel;
	private static DefaultTableModel errorModel;
	private static int position = 0;
	private static Map<String, Integer> symbolmap = new HashMap<>();
    private static String[] key = {"char", "long", "short", "float", "double", "const",
			"boolean", "void", "null", "false", "true",  "int", "do",
			"while", "if", "else", "for", "then", "break", "continue", "class",
			"static", "final",  "return", "signed", "struct",
			"goto", "switch", "case", "default","extern", "sizeof", "typedef", "proc","integer",
			"record","real","call","and","or","not","char", "long", "short", "float", "double", "const",
			"boolean", "void", "null", "false", "true",  "int", "do",
			"while", "if", "else", "for", "then", "break", "continue", "class",
			"static", "final", "return", "signed", "struct","String",
			 "goto", "switch", "case", "default","extern",  "sizeof", "typedef",
			 "proc","integer","record","real","call","and","or","not"};
	private static char[] op = {'+','-','*','<', '>', '&', '|', '~',  
	         '^', '!', '(', ')', '[', ']', '{', '}', '%', ';', ',', '#', '='};
	
	private static char sides[] = { ',', ';', '[', ']', '(', ')', '{', '}', '='};
	
	
	public Analysis(String word,DefaultTableModel tokenListTbModel,DefaultTableModel charListTbModel,DefaultTableModel errorListTbModel) {
		this.words = word;
		Analysis.tokenModel = tokenListTbModel;
		Analysis.charModel = charListTbModel;
		Analysis.errorModel = errorListTbModel;
	}
	
	public void run() {
		String[] lines = words.split("\n");
		symbolmap.clear();
		position = 0;
		for(int x = 0; x < lines.length; x++)
		{
			String str = lines[x];
			if(str.equals(""))
				continue;
			else {
				char[] line = str.toCharArray();
				for(int i = 0; i < line.length; i++) 
				{
					char ch = line[i];
					String token = "";
					if (isword(ch)) // �жϱ�ʶ�͹ؼ���
					{
						i = handleword(ch, token, i, line);
					} else if (isnumber(ch)) // �ж����ֳ���
					{
						i = handledigit(ch, token, i, line, x);
					} else if (isOp(ch)) // ʶ��������ͽ��
					{
						i = handleop(ch, token, i, line, x);
						if (i == -1) {
							break;
						}
					} else if (ch == '"') // ʶ���ַ�������
					{
						i = handlestrs(ch, token, i, line, x);
					} else if (ch == '\'') // ʶ���ַ�����
					{
						i = handlechar(ch, token, i, line, x);
					} else if (ch == '/') // ʶ��ע��
					{
						i = handlecomment(ch, token, i, line, x, str);
						if (i == -1) {
							break;
						}
					} else {
						handleerror(ch,x);
					}
				}
			}
		}
		
	}
	
	//�����ʶ���͹ؼ���
	public static int handleword(char ch,String token,int i,char[] charline){
        do {  
            token += ch;  
            i++;  
            if(i >= charline.length) break;  
            ch = charline[i];  
        } while (ch != '\0' && (isword(ch) || isnumber(ch)));  

        //�ǹؼ��� 
        if (iskey(token.toString()))   
        {  
        	tokenModel.addRow(new Object[] {token, "�ؼ���", "1", "-"});
        }
        //�Ǳ�ʶ��
        else {
        	//������ű�Ϊ�ջ���ű��в�������ǰtoken�������
        	if (symbolmap.isEmpty() || (!symbolmap.isEmpty() && !symbolmap.containsKey(token))) 
        	{  
                symbolmap.put(token, position);
                charModel.addRow(new Object[] {token, position});
                position++;
            }
        	tokenModel.addRow(new Object[] {token, "��ʶ��", "2", token});
        }
        --i; //����ָ���1,ָ�����  
        return i;
	}
	
	//��������������
	public static int handledigit(char ch,String token,int i,char[] charline,int m) {
		//��ʼ������1״̬
		int state = 1;
		//������������
		int k;
        Boolean isfloat = false;  
        while ( (ch != '\0') && (isnumber(ch) || ch == '.' || ch == 'e' || ch == '-'))
        {
        	if (ch == '.' || ch == 'e')  
              isfloat = true;
        	  
            for (k = 0; k <= 6; k++) 
            {  
                char tmpstr[] = digitDFA[state].toCharArray();  
                if (ch != '#' && 1 == in_digitDFA(ch, tmpstr[k])) 
                {  
                    token += ch;  
                    state = k;  
                    break;  
                }  
            }
            if (k > 6) break;
            //����������ǰ�ƶ�
            i++;
            if(i>=charline.length) break;  
            ch = charline[i]; 
        }
        Boolean haveMistake = false;  
        
        if (state == 2 || state == 4 || state == 5) 
        {  
            haveMistake = true;  
        } 
        
        else//1,3,6  
        {  
            if ((!isOp(ch) || ch == '.') && !isnumber(ch) && ch != ' ')  
                haveMistake = true;  
        }  
        
        //������ 
        if (haveMistake)  
        {  
        	//һֱ�����ɷָ���ַ�����  
        	while (ch != '\0' && ch != ',' && ch != ';' && ch != ' ')
            {  
                token += ch;  
                i++;
                if(i >= charline.length) break;  
                ch = charline[i];  
            }
        	errorModel.addRow(new Object[] {m+1, token + " �����쳣"});
        }
        else 
        {  
            if (isfloat) 
            {
            	tokenModel.addRow(new Object[] {token, "������", "3", token});
            } 
            else
            {
            	tokenModel.addRow(new Object[] {token, "����", "3", token});
            }  
        }
        i--;
        return i;
	}
	
	//�����ַ��ͳ���
	public static int handlechar(char ch, String token, int i, char[] charline, int m) {
		//��ʼ��״̬Ϊ0
		int state = 0;				        
        //token���ϡ�
        token += ch;
        
        while (state != 3) {  
            i++;
            if(i >= charline.length) break;
            ch = charline[i]; 
            for (int k = 0; k < 4; k++) 
            {  
                char tmpstr[] = charDFA[state].toCharArray();  
                if (in_charDFA(ch, tmpstr[k])) 
                {            
                    token += ch;
                    state = k;  
                    break;  
                }  
            }  
        }
        if (state != 3) {  
        	errorModel.addRow(new Object[] {m+1, token + " �ַ���������δ���"});
            i--;  
        } 
        else 
        {
        	tokenModel.addRow(new Object[] {token, "�ַ�����", "3", token});
        	
        }     
        return i;
	}
	
	//�����ַ����ͳ���
	public static int handlestrs(char ch, String token, int i, char[] charline, int m) {
		String string = "";  
        string += ch;  

        int state = 0;  
        Boolean haveMistake = false;
		
        while (state != 3 ) {  
            i++; 
            if(i>=charline.length-1) {  
                haveMistake = true;  
                break;  
            }
            ch = charline[i];
            if (ch == '\0') {  
                haveMistake = true;  
                break;  
            }
            for (int k = 0; k < 4; k++) {  
                char tmpstr[] = stringDFA[state].toCharArray();  
                if (in_stringDFA(ch, tmpstr[k])) {  
                    string += ch;  
                    if (k == 2 && state == 1) {  
                        if (isEsSt(ch)) //��ת���ַ�  
                            token = token + '\\' + ch;  
                        else  
                            token += ch;  
                    } else if (k != 3 && k != 1)  
                        token += ch;  
                    state = k;  
                    break;  
                }  
            }  
        }
        if (haveMistake) {
        	errorModel.addRow(new Object[] {m+1, string + " �ַ�����������δ���"});
            --i;  
        } else {
        	tokenModel.addRow(new Object[] {token, "�ַ�������", "3", token});
        }  
        return i;
	}
	
	//����������ͽ��
	public static int handleop(char ch, String token, int i, char[] charline, int m) {
		token += ch;
        if (equaland(ch))  
        {  
            i++;
            if(i>=charline.length) {
            	return -1;
            }
            ch = charline[i];  
            if (ch == '=')  
                token += ch;  
            else 
            {  
            	if (twosame(charline[i - 1]) && ch == charline[i - 1])  
                    token += ch;  
                else  
                    --i;   
            }  
        }
        //�ж��Ƿ�Ϊ���
        if(token.length() == 1)
        {
        	char signal = token.charAt(0);
        	boolean isbound = false;
        	for(int bound = 0; bound < sides.length; bound++)
        	{
        		if(signal == sides[bound])
        		{
        			tokenModel.addRow(new Object[] {token, "���", "4", "-"});
                    isbound = true;
                    break;
        		}
        	}
        	if(!isbound)
        	{
        		tokenModel.addRow(new Object[] {token, "�����", "5", "-"});
        	}
        }
        else
        {
        	tokenModel.addRow(new Object[] {token, "�����", "5", "-"});
        }
        
        return i;
	}
	
	//ʶ��ע��
	public static int handlecomment(char ch, String token, int i, char[] charline, int m, String str) {
		token += ch;
		i++;
		if (i >= charline.length) {
			return -1;
		}
		ch = charline[i];

		// ���Ƕ���ע�ͼ�����ע��
		if (ch != '*' && ch != '/') {
			if (ch == '=')
				token += ch; // /=
			else {
				--i; // ָ����� // /
			}
			tokenModel.addRow(new Object[] { token, "�����", "5", "-" });
			token = "";
		}
		else {
			Boolean haveMistake = false;
			int State = 0;
			if (ch == '*') {
				// ch == '*'
				token += ch;
				int state = 2;

				while (state != 4) {
					i++;
					if (i >= charline.length)
						break;
					ch = charline[i];

					if (ch == '\0') {
						haveMistake = true;
						break;
					}
					for (int k = 2; k <= 4; k++) {
						char tmpstr[] = noteDFA[state].toCharArray();
						if (in_noteDFA(ch, tmpstr[k], state)) {
							token += ch;
							state = k;
							break;
						}
					}
				}
				State = state;
			} else if (ch == '/') {
				int index = str.lastIndexOf("//");

				String tmpstr = str.substring(index);
				int tmpint = tmpstr.length();
				for (int k = 0; k < tmpint; k++)
					i++;
				token = tmpstr;
			}
			if (haveMistake || State != 4) {
				errorModel.addRow(new Object[] { m + 1, "ע��δ���" });
				--i;
			} else {
				tokenModel.addRow(new Object[] { token, "ע��", "6", "-" });
			}
		}
		return i;
	}
	
	public static void handleerror(char ch,int m) {
		if (ch != ' ' && ch != '\t' && ch != '\0' && ch != '\n' && ch != '\r') {
			errorModel.addRow(new Object[] { m + 1, "���ڲ��Ϸ��ַ�" });
		}
	}
	
	//�ж���ĸ���»���
	public static Boolean isword(char ch)
	{
	    return ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || ch == '_');
	}

	public static Boolean isnumber(char ch)
	{  
        return (ch >= '0' && ch <= '9');  
    }
	//�ж��Ƿ�������� 
	public static Boolean isOp(char ch) 
    {  
        for (int i = 0; i < op.length; i++)  
            if (ch == op[i]) {  
                return true;  
            }  
        return false;  
    }
	
	public static Boolean iskey(String str) {  
        Boolean flag = false;  
        for (int i = 0; i < key.length; i++) {  
            if (str.equals(key[i])) {  
                flag = true;  
                break;  
            }  
        }  
        return flag;  
    }
	
	public static Boolean equaland(char ch)   
    {  
        return ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '='  
                || ch == '>' || ch == '<' || ch == '&' || ch == '|'  
                || ch == '^' || ch== '!';  
    }

	public static Boolean twosame(char ch)   
    {  
        return ch == '+' || ch == '-' || ch == '&' || ch == '|';  
    }
	//�ַ���dfa
	public static String stringDFA[] = { 
		"#\\b#", 
		"##a#", 
		"#\\b\"", 
		"####" };
	public static Boolean in_stringDFA(char ch, char key) {  
        if (key == 'a')  
            return true;  
        if (key == '\\')  
            return ch == key;  
        if (key == '"')  
            return ch == key;  
        if (key == 'b')  
            return ch != '\\' && ch != '"';  
        return false;  
    }
	//�ַ�dfa
	public static String charDFA[] = { 
		"#\\b#", 
		"##a#", 
		"###\'", 
		"####" }; 
	public static Boolean isEsSt(char ch) {  
        return ch == 'a' || ch == 'b' || ch == 'f' || ch == 'n' || ch == 'r'  
                || ch == 't' || ch == 'v' || ch == '?' || ch == '0';  
    }
	public static Boolean in_charDFA(char ch, char key) {  
        if (key == 'a')  
            return true;  
        if (key == '\\')  
            return ch == key;  
        if (key == '\'')  
            return ch == key;  
        if (key == 'b')  
            return ch != '\\' && ch != '\'';  
        return false;  
    }
	//����ע��DFA
	public static String noteDFA[] = { 
		"#####", 
		"##*##", 
		"##c*#", 
		"##c*/", 
		"#####" };
	
	public static Boolean in_noteDFA(char ch, char nD, int s) {  
        if (s == 2) {  
            if (nD == 'c') 
            {  
                if (ch != '*') return true;  
                else return false;  
            }  
        }  
        if (s == 3) {  
            if (nD == 'c') {  
                if (ch != '*' && ch != '/') return true;  
                else return false;  
            }  
        }  
        return (ch == nD) ? true : false;  
    }
    //ʶ������DFA
	public static String digitDFA[] = { 
		"#d#####", 
		"#d.#e##", 
		"###d###", 
		"###de##",  
        "#####-d", 
        "######d", 
        "######d" };
	//�ж���������Ƿ����״̬��
	public static int in_digitDFA(char ch, char test) 
	{  
        if (test == 'd') {  
            if (isnumber(ch))  
                return 1;  
            else  
                return 0;  
        }  
        else
        {
        	if (ch == test)
        		return 1;
        	else
        		return 0;
        }
    }
}
