package application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
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
		String[] fieldRecord = fieldName.split(",");
		log.appendText("\nフィールド行の長さ："+ fieldRecord.length);
		dataList.remove(0); // 先頭のフィールド名を削除
		//それぞれのフィールドラベル
		String bp_label = stringRecToString(cutter(fieldRecord,1,4));
		String se_label =stringRecToString(cutter(fieldRecord,5,14));
		String k18_label = stringRecToString(cutter(fieldRecord,15,32));
		String adp_label =stringRecToString(cutter(fieldRecord,33,76));
		
		//出力されるべき内容を保持したListをつくっておいて、すべて以下のループで処理する。
		//ただし、上のフィールドラベルは保持しない。回答データと合計だけ。
		List<String> outputList = new ArrayList<String>();
		for (String s : dataList) {
			String[] allArray = s.split(",");
			//check
			if(allArray.length !=78) {
				log.appendText("Error:"+ allArray[0]);
			}
			CStudent stu = new CStudent(allArray[0]);
			//書き込む際に学籍番号をString の先頭にいれればよいはなし。
			// 学籍番号以外の回答を String からint に変換
			// 1-4は基本属性。5-14自尊感情。15-32Kiss-18, 33-76学校適応感
			// revItem には先頭にダミーを入れているので、レコードの指標は同じでよい。
			
			String[] bp = cutter(allArray,1,4);//基本属性
			//基本属性には合計点処理が必要ないので、そのままString[]でおいておく
			//回答の切り分け.ただし、回答内容についてはそのまま書き出すことになるので、
			//allArray配列の対応フィールドは別に取っておく必要がある。配列である必要もないので、文字列にする
			String se = stringRecToString(cutter(allArray,5,14)); //自尊感情
			String k18 = stringRecToString(cutter(allArray,15,32)); //kiss-18
			String adp = stringRecToString(cutter(allArray,33,76));//適応感
			//逆転項目処理をした int配列は合計点処理がされる。
			int[] seValue = strToInt(allArray,5,14);
			//合計処理
			int seSum = sum(seValue);
			//書き出しのための文字列処理
			String outputStr = allArray[0]+se+","+seSum;
			outputList.add(outputStr);
		}
		// 変換テスト
//		int in = 5;
//		log.appendText("\n入力  "+ in);
//		int out = transForm(in);
//		log.appendText("\n出力  "+out);
		// ファイル書き出しテスト
		saveFile("自尊感情", outputList, "学籍番号"+se_label+",合計");
	}// end of execAction()

	// 変換する際に逆転項目をちゃんと処理するために
	//revItem を対応する長さに切った配列 rev も与える。
	private int[] strToInt(String[] in, int[] rev) {
		//in は回答が入った文字列配列。
		int[] r = new int[in.length];
		int v = 0;
		for(int i=0;i<r.length;i++) {
			try {
				v = Integer.parseInt(in[i]);
				if(rev[i]==1) {
					v=transForm(v);
				}
			}catch(NumberFormatException e){
				v=0;
			}
			r[i] = v;
		}
		return r;
	}
	//別メソッド。
	private int[] strToInt(String[] in, int start, int end) {
		//ここでの in は allArray であることが前提となる
		//自尊感情等の領域の長さの配列に回答をString[] として渡す。
		String[] s = cutter(in,start,end); 
		//ここまでで、s[] には回答がそのまま。文字列だ。
		//revItem はここからでも見えるので、cutter をここで使う。
		int[] rev = cutter(revItem,start,end);
		//rev には逆転項目フラグがはいった。
		//逆転項目の処理
		int[] r = strToInt(s,rev);
		return r;
	}
	//配列カッター。ここで予め必要な長さに切っておく。文字列配列であることに注意
	private String[] cutter(String[] in , int start, int end) {
		String[] r = new String[end-start+1];
		for(int i=0;i<r.length;i++) {
			r[i] = in[start+i];
		}	
		return r;
	}
	//配列カッター int 版
	private int[] cutter(int[] in , int start, int end) {
		int[] r = new int[end-start+1];
		for(int i=0;i<r.length;i++) {
			r[i] = in[start+i];
		}	
		return r;
	}
	//int[] をカンマ区切りのStringに変換
	private String intRecToString(int[] in) {
		String r ="";
		for(int d: in) {
			r += ","+d;
		}
		return r;
	}
	//String[] をカンマ区切りのStringに変換
	private String stringRecToString(String[]in) {
		String r ="";
		for(String d: in) {
			r += ","+d;
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
			FileOutputStream fos = new FileOutputStream(outFile);
			OutputStreamWriter osw = new OutputStreamWriter(fos,"Shift-JIS");
			//FileWriter fw = new FileWriter(outFile);
			BufferedWriter bw = new BufferedWriter(osw);
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
	//配列を確認するためのプリンタ
	private void printRec(int[] in) {
		for(int d:in) {
			System.out.println(d);
		}
	}
	
	//合計処理
	private int sum(int[] in) {
		int r = 0;
		for(int d:in) {
			r+=d;
		}
		return r;
	}

}
