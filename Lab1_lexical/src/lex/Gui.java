package lex;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.RowSorter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import lex.Analysis;

public class Gui extends JFrame{
	
	private DefaultTableModel tokenListTbModel;
	private DefaultTableModel charListTbModel;
	private DefaultTableModel errorListTbModel;
	private JTable jTable1;
	private JTable jTable3;
	private JScrollPane jScrollPane1;
	private JTextArea jTextArea1;
	
	public static final long serialVersionUID = 1L;
	
	public Gui() {
		this.setBounds(100, 100,1080,950);
		initial();
	}
	
	public void initial() {
		Dimension   screensize   =   Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize=this.getSize();
		this.setLocation((screensize.width-frameSize.width)/2,(screensize.height-frameSize.height)/2);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.getContentPane().setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBounds(0, 0, 1080, 879);
		this.getContentPane().add(panel);
		panel.setLayout(null);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBounds(23, 13, 1043, 853);
		panel.add(panel_1);
		panel_1.setLayout(null);
		
		
		jTextArea1 = new JTextArea();
		jScrollPane1 = new JScrollPane(jTextArea1);
		panel_1.add(jScrollPane1);
		jScrollPane1.setBounds(14, 77, 350, 350);
		
		final JButton btnNewButton_2 = new JButton("文件");
		btnNewButton_2.addActionListener(new ActionListener() {        
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jMenuItem1ActionPerformed(evt);
			} 
			}); 
		btnNewButton_2.setBounds(42, 30, 107, 34);
		panel_1.add(btnNewButton_2);
		
		JButton btnNewButton_1 = new JButton("词法编译");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jMenuItem2ActionPerformed(evt);
			}
		});
		btnNewButton_1.setBounds(186, 30, 92, 34);
		panel_1.add(btnNewButton_1);
		
		
	    tokenListTbModel = new DefaultTableModel(
				new Object[][] {},
				new String[] {
						"字符串","类型名","种别码","属性值"
				}
			);
	    jTable1 = new JTable();
	    jTable1.setBackground(new Color(224, 255, 255));
	    jTable1.setFillsViewportHeight(true);
	    jTable1.setModel(tokenListTbModel);
	    RowSorter<DefaultTableModel> sorter = new TableRowSorter<DefaultTableModel>(tokenListTbModel);
	    jTable1.setRowSorter(sorter); 
		JScrollPane tokenSP = new JScrollPane();
		tokenSP.setViewportView(jTable1);
		tokenSP.setBounds(450, 77, 461, 372);
		panel_1.add(tokenSP);
	    
		
	    errorListTbModel = new DefaultTableModel(
				new Object[][] {},
				new String[] {
						"Error at Line","错误说明"
				}
			);
	    
	    JTable jTable2 = new JTable();
	    jTable2.setBackground(new Color(224, 255, 255));
	    jTable2.setFillsViewportHeight(true);
	    jTable2.setModel(errorListTbModel);
		
		RowSorter<DefaultTableModel> sorter2 = new TableRowSorter<DefaultTableModel>(errorListTbModel);
		jTable2.setRowSorter(sorter2); 
		JScrollPane errorSP = new JScrollPane();
		errorSP.setViewportView(jTable2);
		errorSP.setBounds(14, 550, 400, 260);
		panel_1.add(errorSP);
		
		
		charListTbModel = new DefaultTableModel(
				new Object[][] {},
				new String[] {
						"符号","序号"
				}
			);
		jTable3 = new JTable();
	    jTable3.setBackground(new Color(224, 255, 255));
	    jTable3.setFillsViewportHeight(true);
	    jTable3.setModel(charListTbModel);
		
		RowSorter<DefaultTableModel> sorter3 = new TableRowSorter<DefaultTableModel>(charListTbModel);
		jTable3.setRowSorter(sorter3); 
		JScrollPane charSP = new JScrollPane();
		charSP.setViewportView(jTable3);
		charSP.setBounds(500, 477, 241, 372);
		panel_1.add(charSP);
		
		JLabel lblToken = new JLabel("TOKEN");
		lblToken.setBounds(460, 70, 254, 34);
		panel_1.add(lblToken);
		
		JLabel lblErrorTable = new JLabel("错误分析");
		lblErrorTable.setBounds(14, 540, 203, 41);
		panel_1.add(lblErrorTable);
		
		JLabel lblDfa = new JLabel("符号表");
		lblDfa.setBounds(500, 470, 285, 47);
		panel_1.add(lblDfa);
	}
	
	private void jMenuItem1ActionPerformed(ActionEvent evt) {
		// TODO add your handling code here:

		FileDialog fileDialog;
		//An abstract representation of file and directory pathnames. 
		File file = null;
		Frame frame = null;
		fileDialog = new FileDialog(frame, "Open", FileDialog.LOAD);
		fileDialog.setVisible(true);

		try {
			jTextArea1.setText("");
			file = new File(fileDialog.getDirectory(), fileDialog.getFile());
			FileReader filereader = new FileReader(file);
			BufferedReader bufferreader = new BufferedReader(filereader);
			String aline;
			while ((aline = bufferreader.readLine()) != null)

				jTextArea1.append(aline + "\r\n");
			filereader.close();
			bufferreader.close();

		} catch (IOException e) {
			System.out.println(e);
		}
	}
	
	private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {
		
		
		String program = jTextArea1.getText();
		//创建词法分析类
		Analysis ana = new Analysis(program, tokenListTbModel,charListTbModel,errorListTbModel);
		ana.run();
	}
}
