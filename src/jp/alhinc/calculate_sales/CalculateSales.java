package jp.alhinc.calculate_sales;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
//ArrayList,Listをimportした。
import java.util.List;
import java.util.Map;
public class CalculateSales {

	// 支店定義ファイル名
	private static final String FILE_NAME_BRANCH_LST = "branch.lst";

	// 支店別集計ファイル名
	private static final String FILE_NAME_BRANCH_OUT = "branch.out";

	// エラーメッセージ
	private static final String UNKNOWN_ERROR = "予期せぬエラーが発生しました";
	private static final String FILE_NOT_EXIST = "支店定義ファイルが存在しません";
	private static final String FILE_INVALID_FORMAT = "支店定義ファイルのフォーマットが不正です";

	/**
	 * メインメソッド
	 *
	 * @param コマンドライン引数
	 */
	public static void main(String[] args) {
		// 支店コードと支店名を保持するMap
		Map<String, String> branchNames = new HashMap<>();
		// 支店コードと売上金額を保持するMap
		Map<String, Long> branchSales = new HashMap<>();

		// 支店定義ファイル読み込み処理
		if(!readFile(args[0], FILE_NAME_BRANCH_LST, branchNames, branchSales)) {
			return;
		}

		// ※ここから集計処理を作成してください。(処理内容2-1、2-2)
		File[] files = new File(args[0]).listFiles();
		//リスト作成
		List<File> rcdFiles = new ArrayList<>();
		//BufferedReaderを初期化
		BufferedReader br = null;
		//filesの数だけ繰り返す
		for(int i = 0; i < files.length; i++) {
			//ファイルの名前を取得とstring型に変更
			String name = files[i].getName();



			//売上ファイルのみをrcdFiles(リスト)に加える
			if(name.matches("^[0-9]{8}.rcd$")) {
				rcdFiles.add(files[i]);
			}
		}

			//ファイルを開いて中身がないときのためのtry catch文？
			try {
				//リストの数だけ繰り返す
				for(int j = 0; j < rcdFiles.size(); j++) {


					//ファイルを読み込む準備
					FileReader fr = new FileReader(rcdFiles.get(j));
					br = new BufferedReader(fr);
					//初期化とリスト作成
					String rcdLine;
					List<String> salesList = new ArrayList<>();
					//ファイルが読み込めなくなるまで行うため
					while((rcdLine = br.readLine()) != null) {
						//リストに追加
						salesList.add(rcdLine);

					}
					//型を	longに変更
					long fileSale = Long.parseLong(salesList.get(1));
					// branchSalesにfilesaleを追加
					Long saleAmount = branchSales.get(salesList.get(0)) + fileSale;
					//saleAmountをbrancSalesに戻す
					branchSales.put(salesList.get(0), saleAmount);





				}
			} catch(IOException e) {
				System.out.println(UNKNOWN_ERROR);
				return;
			} finally {
			// ファイルを開いている場合
				if(br != null) {
					try {
					// ファイルを閉じる
						br.close();
					} catch(IOException e) {
						System.out.println(UNKNOWN_ERROR);
						return;
					}
				}
			}




		// 支店別集計ファイル書き込み処理
		if(!writeFile(args[0], FILE_NAME_BRANCH_OUT, branchNames, branchSales)) {
			return;}

	}



	/**
	 * 支店定義ファイル読み込み処理
	 *
	 * @param フォルダパス
	 * @param ファイル名
	 * @param 支店コードと支店名を保持するMap
	 * @param 支店コードと売上金額を保持するMap
	 * @return 読み込み可否
	 */
	private static boolean readFile(String path, String fileName, Map<String, String> branchNames, Map<String, Long> branchSales) {
		BufferedReader br = null;

		try {
			File file = new File(path, fileName);
			FileReader fr = new FileReader(file);
			br = new BufferedReader(fr);

			String line;
			// 一行ずつ読み込む
			while((line = br.readLine()) != null) {
				// ※ここの読み込み処理を変更してください。(処理内容1-2)
				// , で分割してitemsに入れる
				String[] items = line.split(",");
				//keyを使ってvauleに加える
				branchNames.put(items[0], items[1]);
				branchSales.put(items[0], 0L);
			}

		} catch(IOException e) {
			System.out.println(UNKNOWN_ERROR);
			return false;
		} finally {
			// ファイルを開いている場合
			if(br != null) {
				try {
					// ファイルを閉じる
					br.close();
				} catch(IOException e) {
					System.out.println(UNKNOWN_ERROR);
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 支店別集計ファイル書き込み処理
	 *
	 * @param フォルダパス
	 * @param ファイル名
	 * @param 支店コードと支店名を保持するMap
	 * @param 支店コードと売上金額を保持するMap
	 * @return 書き込み可否
	 */
	private static boolean writeFile(String path, String fileName, Map<String, String> branchNames, Map<String, Long> branchSales) {
		// ※ここに書き込み処理を作成してください。(処理内容3-1)
		BufferedWriter bw = null;

		try {
			//ファイルに書き込み準備
			File file = new File(path, fileName);
			FileWriter fw = new FileWriter(file);
			bw =new BufferedWriter(fw);


			//拡張for文を使いmapからkeyを取得
			for(String key:branchNames.keySet()) {
				//型をStringに変更
				String salesAmount = Long.toString(branchSales.get(key));
				//ファイルに書き込み
				bw.write(key);
				bw.write(",");
				bw.write(branchNames.get(key));
				bw.write(",");
				bw.write(salesAmount);

				bw.newLine();
				}


		} catch(IOException e) {
			System.out.println(UNKNOWN_ERROR);
			return false;
		} finally {
			// ファイルを開いている場合
			if(bw != null) {
				try {
					// ファイルを閉じる
					bw.close();
				} catch(IOException e) {
					System.out.println(UNKNOWN_ERROR);
					return false;
				}
			}
		}
		return true;

	}

}
