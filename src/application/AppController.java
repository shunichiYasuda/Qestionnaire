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
	// アンケートデータをためておくリスト
	List<String> dataList = new ArrayList<String>();
	// ファイルの場所を記憶する String
	String dir = ".";
	// このファイルに入っている学生のリスト
	List<CStudent> stuList = new ArrayList<CStudent>();
	// 逆転項目フラグ。先頭の0は学籍番号列に対応したダミー。
	int[] revItem = { 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 1, 1, 1, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0,
			1, 0, 0, 1, 0, 0, 0, 1 };

	@FXML
	void quitAction() {
		System.exit(0);
	}

	@FXML
	void openAction() {
		FileChooser fc = new FileChooser();
		File file;
		fc.setTitle("open data file");
		if (dir != null) {
			fc.setInitialDirectory(new File(dir));
			file = fc.showOpenDialog(null);
		} else {
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
		log.appendText("データ総数：" + (dataList.size() - 1));
		String fieldName = dataList.get(0);
		dataList.remove(0); // 先頭のフィールド名を削除
		for (String s : dataList) {
			String[] allArray = s.split(",");
			CStudent stu = new CStudent(allArray[0]);
			// 学籍番号以外の回答を String からint に変換
			// 1-4は基本属性。5-14自尊感情。15-32Kiss-18, 33-76学校適応感
			// revItem には先頭にダミーを入れているので、レコードの指標は同じでよい。
			//回答の切り分け
			String[] bp = new String[4];//基本属性
			String[] se = new String[10]; //自尊感情
			String[] k18 = new String[18]; //kiss-18
			String[] adp = new String[44];//適応感
			//bp = strToInt(allArray,1,4);
			stuList.add(stu);
		}
		//
		List<String> outputList = new ArrayList<String>();
		for (CStudent s : stuList) {
			outputList.add(s.getId());
		}
		// 変換テスト
//		int in = 5;
//		log.appendText("\n入力  "+ in);
//		int out = transForm(in);
//		log.appendText("\n出力  "+out);
		// ファイル書き出しテスト
		saveFile("基本属性", outputList, "学籍番号");
	}// end of execAction()

	// 回答は長いString[] になっているので、ここでそれぞれの長さに区切る
	// 変換する際に逆転項目をちゃんと処理する。長さが異なるから始めと終わりの場所もいれておく。
	private int[] strToInt(String[] in, int start, int end) {
		//in は全回答だ。
		int[] r = new int[end - start +1];
		int v = 0;
		for (int i=0;i<r.length;i++) {
			try {
				v = Integer.parseInt(in[i]);
			} catch (NumberFormatException e) {
				v = 0;
			}
			r[i] = v;
		}
		return r;

	}

	// 逆転項目の得点処理
	private int transForm(int in) {
		int r = 0;
		int[] transRec = { 5, 4, 3, 2, 1 };
		// 得点が5 なら1,4なら2、3は変わらず 2なら4、1なら5
		int pos = in - 1; // 元得点による配列上の位置。
		r = transRec[pos];
		return r;
	}

	// ファイル書き出し共通
	private void saveFile(String title, List<String> list, String field) {
		String filename = dir + "\\" + title + ".csv";
		log.appendText("\n" + filename);
		File outFile = new File(filename);
		try {
			FileWriter fw = new FileWriter(outFile);
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter pw = new PrintWriter(bw);
			pw.println(field);
			for (String s : list) {
				pw.println(s);
			}
			pw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	} // end of saveFile()

	// テーブルを縦に分割して、

}
