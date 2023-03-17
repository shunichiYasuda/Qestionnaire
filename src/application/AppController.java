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
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

public class AppController {
	@FXML
	Button openBtn;
	@FXML
	Button execBtn;
	@FXML
	Button quitBtn;
	@FXML 
	Button clearBtn;
	@FXML
	TextArea log;
	@FXML
	TextField enterYear;
	@FXML
	TextField ansYear;
	//
	// アンケートデータをためておくリスト
	List<String> dataList = new ArrayList<String>();
	// ファイルの場所を記憶する String
	String dir = ".";
	// 逆転項目フラグ。先頭の0は学籍番号列に対応したダミー。
	int[] revItem = { 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 1, 1, 1, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0,
			1, 0, 0, 1, 0, 0, 0, 1 };
	String[] fieldRecord;// 基本属性から学校適応感まですべてのファイルの先頭にここから切り出したラベルを付ける

	//クリアボタン
	@FXML
	void clearAction() {
		log.clear();
		dataList.clear();
	}
	//入学年度と回答時期を TexField から読み取り、ファイル名に付加する。
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
		dir = file.getParent();
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
		log.appendText(file.getAbsolutePath()+"\n");
	}

	@FXML
	void execAction() {
		//入学年度と回答時期
		String enterY = enterYear.getText();
		String ansY = ansYear.getText();
		log.appendText("入学年度："+enterY+"\t"+"回答年次："+ansY+ "\n");
		//ファイル名につける文字列
		String topStr = enterY+"年度入学生"+ansY+"回生_";
		log.appendText("データ総数：" + (dataList.size() - 1));
		String fieldName = dataList.get(0);
		fieldRecord = fieldName.split(",");
		log.appendText("\nフィールド行の長さ：" + fieldRecord.length + "\n");
		dataList.remove(0); // 先頭のフィールド名を削除
		process(topStr+"自尊感情", 5, 14);
		process(topStr+"Kiss-18", 15, 32);
		process(topStr+"学校適応感", 33, 76);
		//基本属性だけは処理が別。逆転項目がないし、空白処理もしないので。
		//基本属性ラベル
		//String label = stringRecToString(cutter(fieldRecord, 1, 4));
		String label = ",性別,出身地,部活経験,志望順位";
		log.appendText(label);
		
		List<String> bpList = new ArrayList<String>();
		for (String s : dataList) {
			String[] allArray = s.split(",");
			// check
			if (allArray.length != 78) {
				log.appendText("Error:" + allArray[0]);
			}
			String bp = stringRecToString(cutter(allArray, 1, 4));// 基本属性
			// 書き出しのための文字列処理
			String outputStr = allArray[0] + bp;
			bpList.add(outputStr);
		} // end of dataList を1行ずつ処理するループ

		saveFile(topStr+"基本属性", bpList, "学籍番号" + label);
	}// end of execAction()

	// 処理がかなりムダ。切り分けるフィールド番号を与えて、すべてそこで終わらす処理を考えた方がよいかも。
	private void process(String title, int start, int end) {
		String label = stringRecToString(cutter(fieldRecord, start, end));
		// 出力されるべき内容を保持したListをつくっておいて、すべて以下のループで処理する。
		// ただし、上のフィールドラベルは保持しない。回答データと合計だけ。
		// String 配列を使って、自尊感情・kiss-18・学校適応感は、アンケートに未回答があれば、出力しない
		List<String> outputList = new ArrayList<String>();
		for (String s : dataList) {
			String[] allArray = s.split(",");
			String[] ansArray = cutter(allArray, start, end); // 回答が入ったString[]
			// 空白除去のための空白フラグ：true：空白あり、false:空白なし
			boolean spaceFlag = false; // 初期値は空白無し
			spaceFlag = checkSpace(ansArray);
			// とりあえず、ここまでが機能するかどうかのチェック。済み3月17日14:30
			// 空白がない学生だけを処理する
			if (!spaceFlag) {
				// 逆転項目処理をした int配列は合計点処理がされる。
				int[] value = strToInt(allArray, start, end);
				// 合計処理
				int sum = sum(value);
				// 学籍番号と回答と末尾に合計点
				String str = stringRecToString(ansArray);
				outputList.add(allArray[0] + str+","+sum);
			}
		}// end of datList の壱行ずつの処理
//		for(String s : outputList) {
//			log.appendText(s+"\n");
//		}
		// ファイル書き出しテスト
		saveFile(title, outputList, "学籍番号" + label + ",合計");
	}

	// 空白チェック
	private boolean checkSpace(String[] in) {
		// 空白がなければ false, 空白か w があれば trueを返す
		boolean r = false;
		for (String s : in) {
			if (s.equals("") || s.equals("w"))
				r = true;
		}
		return r;
	}

	// 変換する際に逆転項目をちゃんと処理するために
	// revItem を対応する長さに切った配列 rev も与える。
	private int[] strToInt(String[] in, int[] rev) {
		// in は回答が入った文字列配列。
		int[] r = new int[in.length];
		int v = 0;
		for (int i = 0; i < r.length; i++) {
			try {
				v = Integer.parseInt(in[i]);
				if (rev[i] == 1) {
					v = transForm(v);
				}
			} catch (NumberFormatException e) {
				v = 0;
			}
			r[i] = v;
		}
		return r;
	}

	// 別メソッド。
	private int[] strToInt(String[] in, int start, int end) {
		// ここでの in は allArray であることが前提となる
		// 自尊感情等の領域の長さの配列に回答をString[] として渡す。
		String[] s = cutter(in, start, end);
		// ここまでで、s[] には回答がそのまま。文字列だ。
		// revItem はここからでも見えるので、cutter をここで使う。
		int[] rev = cutter(revItem, start, end);
		// rev には逆転項目フラグがはいった。
		// 逆転項目の処理
		int[] r = strToInt(s, rev);
		return r;
	}

	// 配列カッター。ここで予め必要な長さに切っておく。文字列配列であることに注意
	private String[] cutter(String[] in, int start, int end) {
		String[] r = new String[end - start + 1];
		for (int i = 0; i < r.length; i++) {
			r[i] = in[start + i];
		}
		return r;
	}

	// 配列カッター int 版
	private int[] cutter(int[] in, int start, int end) {
		int[] r = new int[end - start + 1];
		for (int i = 0; i < r.length; i++) {
			r[i] = in[start + i];
		}
		return r;
	}

	// int[] をカンマ区切りのStringに変換
	private String intRecToString(int[] in) {
		String r = "";
		for (int d : in) {
			r += "," + d;
		}
		return r;
	}

	// String[] をカンマ区切りのStringに変換
	private String stringRecToString(String[] in) {
		String r = "";
		for (String d : in) {
			r += "," + d;
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
			OutputStreamWriter osw = new OutputStreamWriter(fos, "Shift-JIS");
			// FileWriter fw = new FileWriter(outFile);
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
		// 配列を確認するためのプリンタ

	private void printRec(int[] in) {
		for (int d : in) {
			System.out.println(d);
		}
	}

	// 合計処理
	private int sum(int[] in) {
		int r = 0;
		for (int d : in) {
			r += d;
		}
		return r;
	}

}
