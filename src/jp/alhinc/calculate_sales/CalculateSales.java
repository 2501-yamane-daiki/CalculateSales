package jp.alhinc.calculate_sales;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
//ArrayList,Listをimportした。
import java.util.List;
import java.util.Map;

public class CalculateSales {

	// 支店定義ファイル名
	private static final String FILE_NAME_BRANCH_LST = "branch.lst";
	//商品定義ファイル名
	private static final String FILE_NAME_COMMDITY_LST = "commodity.lst";

	// 支店別集計ファイル名
	private static final String FILE_NAME_BRANCH_OUT = "branch.out";
	private static final String FILE_NAME_COMMDITY_OUT = "commdity.out";

	// エラーメッセージ
	private static final String UNKNOWN_ERROR = "予期せぬエラーが発生しました";
	private static final String BRANCHFILE_NOT_EXIST = "支店定義ファイルが存在しません";
	private static final String BRANCHFILE_INVALID_FORMAT = "支店定義ファイルのフォーマットが不正です";
	private static final String FILE_ORDER = "売上ファイル名が連番になっていません";
	private static final String TOTAL_AMOUNT_EXCEEDED = "合計⾦額が10桁を超えました";
	private static final String NO_BRANCH_CODE = "の支店コードが不正です";
	private static final String INOVALID_FORMAT = "のフォーマットが不正です";
	private static final String COMMODITYFILE_INVALID_FORMAT = "商品定義ファイルのフォーマットが不正です";
	private static final String NO_COMMODITY_CODE = "の商品名コードが不正です";
	private static final String COMMDITYFILE_NOT_EXIST = "商品定義ファイルが存在しません";

	/**
	 * メインメソッド
	 *
	 * @param コマンドライン引数
	 */
	public static void main(String[] args) {
		//コマンドライン引数が渡されているか確認
		if (args.length != 1) {
			System.out.println(UNKNOWN_ERROR);
			return;
		}
		// 支店コードと支店名を保持するMap
		Map<String, String> branchNames = new HashMap<>();
		//商品コードと商品名を保持するMap
		Map<String, String> commodityNames = new HashMap<>();
		// 支店コードと売上金額を保持するMap
		Map<String, Long> branchSales = new HashMap<>();
		//商品コードと商品名を保持するMap
		Map<String, Long> commoditySales = new HashMap<>();

		// 支店定義ファイル読み込み処理
		if (!readFile(args[0], FILE_NAME_BRANCH_LST, branchNames, branchSales, "^[0-9]{3}$", BRANCHFILE_INVALID_FORMAT, BRANCHFILE_NOT_EXIST)) {
			return;
		}
		//商品定義ファイル読み込み処理
		if (!readFile(args[0], FILE_NAME_COMMDITY_LST, commodityNames, commoditySales, "^[A-Za-z1-9]{8}$", COMMODITYFILE_INVALID_FORMAT, COMMDITYFILE_NOT_EXIST)) {
			return;
		}

		// ※ここから集計処理を作成してください。(処理内容2-1、2-2)
		File[] files = new File(args[0]).listFiles();
		//リスト作成
		List<File> rcdFiles = new ArrayList<>();
		//BufferedReaderを初期化
		BufferedReader br = null;
		//filesの数だけ繰り返し
		for (int i = 0; i < files.length; i++) {
			//ファイルの名前を取得とstring型に変更
			String name = files[i].getName();

			//売上ファイルのみをrcdFiles(リスト)に加える
			//ファイルなのか確認
			if (files[i].isFile() && name.matches("^[0-9]{8}.rcd$")) {
				rcdFiles.add(files[i]);
			}
		}
		//売上ファイルを保持しているListを昇順ソート
		Collections.sort(rcdFiles);
		//ファイルが連番になっているか確認
		for (int i = 0; i < rcdFiles.size() - 1; i++) {
			int former = Integer.parseInt(rcdFiles.get(i).getName().substring(0, 8));
			int latter = Integer.parseInt(rcdFiles.get(i + 1).getName().substring(0, 8));

			if ((latter - former) != 1) {
				System.out.println(FILE_ORDER);
				return;
			}
		}

		//ファイルを開いて中身がないときのためのtry catch文？
		//リストの数だけ繰り返す
		for (int i = 0; i < rcdFiles.size(); i++) {
			try {
				//ファイルを読み込む準備
				FileReader fr = new FileReader(rcdFiles.get(i));
				br = new BufferedReader(fr);
				//初期化とリスト作成
				String rcdLine;
				List<String> salesList = new ArrayList<>();
				//ファイルが読み込めなくなるまで行うため
				while ((rcdLine = br.readLine()) != null) {
					//リストに追加
					salesList.add(rcdLine);

				}
				//売上ファイルのフォーマットを確認
				//フォーマットの数を2から３に変更
				if (salesList.size() != 3) {
					System.out.println(rcdFiles.get(i).getName() + INOVALID_FORMAT);
					return;
				}
				//
				//Mapに特定のKeyが存在するか確認する方法支店バージョン
				if ( ! branchNames.containsKey(salesList.get(0))) {
					System.out.println(rcdFiles.get(i).getName() + NO_BRANCH_CODE);
					return;
				}
				//Mapに特定のKeyが存在するか確認する方法商品名バージョン
				if ( ! commodityNames.containsKey(salesList.get(1))) {
					System.out.println(rcdFiles.get(i).getName() + NO_COMMODITY_CODE);
					return;
				}
				////売上金額が数字なのか確認
				if (!salesList.get(2).matches("^[0-9]*$")) {
					System.out.println(UNKNOWN_ERROR);
					return;
				}
				//型をlongに変更
				long fileSale = Long.parseLong(salesList.get(2));

				// branchSalesにfilesaleを追加
				Long saleAmount = branchSales.get(salesList.get(0)) + fileSale;

				//commontySalesにfileSaleを追加
				Long productAmount = commoditySales.get(salesList.get(1)) + fileSale;
				//集計が10桁を超えてないか確認
				if (saleAmount >= 10000000000L) {
					System.out.println(TOTAL_AMOUNT_EXCEEDED);
					return;
				}
				///commontySalesの集計が10桁を超えてないか確認
				if (productAmount >= 10000000000L) {
					System.out.println(TOTAL_AMOUNT_EXCEEDED);
					return;
				}
				//saleAmountをbrancSalesに戻す
				branchSales.put(salesList.get(0), saleAmount);
				//商品合計をcommontySalesに戻す
				commoditySales.put(salesList.get(1), productAmount);
			} catch (IOException e) {
				System.out.println(UNKNOWN_ERROR);
				return;
			} finally {
				// ファイルを開いている場合
				if (br != null) {
					try {
						// ファイルを閉じる
						br.close();
					} catch (IOException e) {
						System.out.println(UNKNOWN_ERROR);
						return;
					}
				}
			}
		}
		// 支店別集計ファイル書き込み処理
		if ( ! writeFile(args[0], FILE_NAME_BRANCH_OUT, branchNames, branchSales)) {
			return;
		}
		if ( ! writeFile(args[0], FILE_NAME_COMMDITY_OUT, commodityNames, commoditySales)) {
			return;
		}
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
	private static boolean readFile(String path, String fileName, Map<String, String> eachNames, Map<String, Long> eachSales, String fileTest, String invalidFormat, String fileNotExist) {
		BufferedReader br = null;

		try {
			File file = new File(path, fileName);
			if (!file.exists()) {
				//ファイルが存在しない場合
				System.out.println(fileNotExist);
				return false;
			}
			FileReader fr = new FileReader(file);
			br = new BufferedReader(fr);

			String line;
			// 一行ずつ読み込む
			while ((line = br.readLine()) != null) {
				// ※ここの読み込み処理を変更してください。(処理内容1-2)
				// , で分割してitemsに入れる
				String[] items = line.split(",");
				//ファイル名が合ってる確認している
				//matchesと出力内容を引数
				if ((items.length != 2) || (!items[0].matches(fileTest))) {
					System.out.println(invalidFormat);
					return false;
				}

				//keyを使ってvauleに加える
				eachNames.put(items[0], items[1]);
				eachSales.put(items[0], 0L);
			}

		} catch (IOException e) {
			System.out.println(UNKNOWN_ERROR);
			return false;
		} finally {
			// ファイルを開いている場合
			if (br != null) {
				try {
					// ファイルを閉じる
					br.close();
				} catch (IOException e) {
					System.out.println(UNKNOWN_ERROR);
					return false;
				}
			}
		}
		return true;
	}

	//商品定義ファイルの読み込み

	/**
	 * 支店別集計ファイル書き込み処理
	 *
	 * @param フォルダパス
	 * @param ファイル名
	 * @param 支店コードと支店名を保持するMap
	 * @param 支店コードと売上金額を保持するMap
	 * @return 書き込み可否
	 */
	private static boolean writeFile(String path, String fileName, Map<String, String> eachNames, Map<String, Long> eachSales) {
		// ※ここに書き込み処理を作成してください。(処理内容3-1)
		BufferedWriter bw = null;

		try {
			//ファイルに書き込み準備
			File file = new File(path, fileName);
			FileWriter fw = new FileWriter(file);
			bw = new BufferedWriter(fw);

			//拡張for文を使いmapからkeyを取得
			for (String key : eachNames.keySet()) {
				//型をStringに変更
				String eachSalesAmount = Long.toString(eachSales.get(key));
				//ファイルに書き込み
				bw.write(key);
				bw.write(",");
				bw.write(eachNames.get(key));
				bw.write(",");
				bw.write(eachSalesAmount);

				bw.newLine();
			}

		} catch (IOException e) {
			System.out.println(UNKNOWN_ERROR);
			return false;
		} finally {
			// ファイルを開いている場合
			if (bw != null) {
				try {
					// ファイルを閉じる
					bw.close();
				} catch (IOException e) {
					System.out.println(UNKNOWN_ERROR);
					return false;
				}
			}
		}
		return true;

	}

}
