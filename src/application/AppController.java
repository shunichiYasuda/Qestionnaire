package application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;

public class AppController {
	@FXML 
	Button openBtn;
	@FXML 
	Button execBtn;
	@FXML
	Button quitBtn;
	@FXML
	TextArea log;
	//
	//アンケートデータをためておくリスト
	List<String> dataList = new ArrayList<String>();
	//ファイルの場所を記憶する String
	String dir = null;
	//このファイルに入っている学生のリスト
	List<CStudent> stuList = new ArrayList<CStudent>();
	//
	int[] revItem = {0,0,0,0,0,0,1,0,1,0,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,1,1,1,0,1,1,1,0,1,1,1,0,0,1,0,1,0,0,1,0,1,1,1,1,0,1,0,0,0,0,0,1,0,1,0,0,1,0,0,0,1};
	
	@FXML
	void quitAction() {
		System.exit(0);
	}
	@FXML
	void openAction() {
		FileChooser fc = new FileChooser();
		File file;
		fc.setTitle("open data file");
		if(dir != null) {
			fc.setInitialDirectory(new File(dir));
			file = fc.showOpenDialog(null);
		}else {
			file = fc.showOpenDialog(null);
			dir = file.getParent();
		}
		try {
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String line = null;
			while ((line = br.readLine()) != null) {
				dataList.add(line);
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@FXML
	void execAction() {
		log.appendText("データ総数："+ (dataList.size()-1) );
		
		//変換テスト
//		int in = 5;
//		log.appendText("\n入力  "+ in);
//		int out = transForm(in);
//		log.appendText("\n出力  "+out);
		//ファイル名テスト
		saveFile("基本属性");
	}
	//逆転項目の得点処理
	private int transForm(int in) {
		int r = 0;
		int[] transRec = {5,4,3,2,1};
		//得点が5 なら1,4なら2、3は変わらず 2なら4、1なら5
		int pos = in - 1; //元得点による配列上の位置。
		r = transRec[pos];
		return r;
	}
	//ファイル書き出し共通
	private void saveFile(String title) {
		String filename = dir + "\\"+title + ".csv";
		log.appendText("\n"+filename);
		File outFile = new File(filename);
		try {
			FileWriter fw = new FileWriter(outFile);
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter pw = new PrintWriter(bw);
			pw.println("This is a sample line.");
			pw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	} //end of saveFile()
	
	//テーブルを縦に分割して、
	
	
	
}
