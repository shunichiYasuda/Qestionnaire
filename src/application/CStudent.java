package application;

public class CStudent {
	String id;
	//基本属性。性別、出身、部活経験、志望順位、ｗと空白は「0」として記録
	int[] basicProperties ;
	//自尊感情。逆転項目の得点処理済みの数値。ｗと空白は「0」として記録
	int[] selfEsteem;
	//Kiss-18。逆転項目の得点処理済みの数値。ｗと空白は「0」として記録
	int[] kiss18;
	//学校適応感。逆転項目の得点処理済みの数値。ｗと空白は「0」として記録
	int[] adaptation;
	//自尊感情、Kiss-18,学校適応感の合計値
	int seScore, kissScore,adapScore;
	//
	public CStudent(String in) {
		this.id = in;
		this.basicProperties =  new int[4];
		this.selfEsteem = new int[10];
		this.kiss18 = new int[18];
		this.adaptation = new int[44];
		seScore=0;
		kissScore=0;
		adapScore=0;
	}
	
	//リストで読み出したアンケート全体のint配列をいっぺんに読み込む。
	public void setRecord(int[] in) {
		for(int i=0;i<in.length;i++) {
			
		}
	}
	//getter
	public String getId() {
		return this.id;
	}
	//setter
	public void setBasicProperties(int[] in) {
		
	}
}
