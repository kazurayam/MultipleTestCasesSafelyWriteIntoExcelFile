import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

import org.apache.poi.hssf.usermodel.HSSFWorkbook

import com.kms.katalon.core.annotation.AfterTestCase
import com.kms.katalon.core.annotation.AfterTestSuite
import com.kms.katalon.core.annotation.BeforeTestCase
import com.kms.katalon.core.annotation.BeforeTestSuite
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.context.TestCaseContext
import com.kms.katalon.core.context.TestSuiteContext
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import internal.GlobalVariable as GlobalVariable
import net.bytebuddy.implementation.bytecode.constant.NullConstant

/**
 * 
 * 
 * @author kazurayam
 *
 */
class MyTestListener {
	
	
	static {
		GlobalVariable.WORKBOOK = null
		GlobalVariable.CURRENT_TESTSUITE_NAME = null
		GlobalVariable.CURRENT_TESTCASE_NAME = null
		//GlobalVariable.REPORT_FOLDER_NAME = null
	}
	
	
	static final Path storageDir = Paths.get(RunConfiguration.getProjectDir()).resolve('Excel files')
	
	/**
	 * if GlobalVariable.WORKBOOK is null, then create an instance of HSSFWorkbook 
	 * and store it into GlobalVariable.WORKBOOK
	 *  
	 * @param gb_workbook
	 */
	static void openWorkbook() {
		if (GlobalVariable.WORKBOOK == null) {
			String excelFileName = resolveExcelFileName()
			Path excelPath = storageDir.resolve(excelFileName)
			HSSFWorkbook wb
			if (Files.exists(excelPath)) { 
				Files.createDirectories(excelPath.getParent())
				FileInputStream fis = new FileInputStream(excelPath.toFile())
				wb = new HSSFWorkbook(fis)
			} else {
				wb = new HSSFWorkbook()
			}
			GlobalVariable.WORKBOOK = wb
			WebUI.comment("#openWorkbook opened Excel file at ${excelPath.toString()}")
		} else {
			WebUI.comment("#openWorkbook GlobalVariable.WORKBOOK was found not null")
			WebUI.comment("#openWorkbook GlobalVariable.WORKBOOK was instance of ${GlobalVariable.WORKBOOK.getClass().getName()}")
			WebUI.comment("#openWorkbook GlobalVariable.WORKBOOK=${GlobalVariable.WORKBOOK}")
		}
	}
	
	static void closeWorkbook(GlobalVariable gb_workbook) {
		if (GlobalVariable.WORKBOOK != null) {
			String excelFileName = resolveExcelFileName()
			Path excelPath = storageDir.resolve(excelFileName)
			Files.createDirectories(excelPath.getParent())
			FileOutputStream fos = new FileOutputStream(excelPath.toFile())
			HSSFWorkbook wb = (HSSFWorkbook)GlobalVariable.WORKBOOK
			wb.write(fos)
			fos.flush()
			fos.close()
			WebUI.comment("#closeWorkbook closed Excel file at ${excelPath.toString()}")
		} else {
			WebUI.comment("#closeWorkbook GlobalVariable.WORKBOOK was found null")
		}
	}
	
	static String resolveExcelFileName() {
		String currentTestSuiteName
		String reportFolderName
		String currentTestCaseName
		if (GlobalVariable.CURRENT_TESTSUITE_NAME != null) {
			currentTestSuiteName = (String)GlobalVariable.CURRENT_TESTSUITE_NAME
			if (GlobalVariable.REPORT_FOLDER_NAME != null) {
				reportFolderName = (String)GlobalVariable.REPORT_FOLDER_NAME
				return "${currentTestSuiteName}.${reportFolderName}.xls"     // 'TS_a.20180901_180938.xls'
			} else {
				throw new IllegalStateException("GlobalVariable.REPORT_FOLDER_NAME is null")
			}
		} else {
			if (GlobalVariable.CURRENT_TESTCASE_NAME != null) {
				currentTestCaseName = (String)GlobalVariable.CURRENT_TESTCASE_NAME
					return "${currentTestCaseName}.xls"
			} else {
				throw new IllegalStateException('GlobalVariable.CURRENT_TESTCASE_NAME is null')
			}
		}
	}
	

	/**
	 * 
	 */
	@BeforeTestSuite
	def beforeTestSuite(TestSuiteContext testSuiteContext) {
		String testSuiteId = testSuiteContext.getTestSuiteId()  // 'Test Suites/TS_a' for example
		GlobalVariable.CURRENT_TESTSUITE_NAME = testSuiteId.substring(testSuiteId.indexOf('/') + 1)   // 'TS_a' for example
		WebUI.comment("#beforeTestSuite GlobalVariable.CURRENT_TESTSUITE_NAME=${GlobalVariable.CURRENT_TESTSUITE_NAME}")
		//
		Path reportFolderPath = Paths.get(RunConfiguration.getReportFolder())
		WebUI.comment("#beforeTestSuite reportFolderPath=${reportFolderPath.toString()}")
		GlobalVariable.REPORT_FOLDER_NAME = reportFolderPath.getFileName().toString()
		WebUI.comment("#beforeTestSuite GlobalVariable.REPORT_FOLDER_NAME=${GlobalVariable.REPORT_FOLDER_NAME}")
		//
		openWorkbook()
	}
	
	/**
	 *
	 */
	@BeforeTestCase
	def beforeTestCase(TestCaseContext testCaseContext) {
		String testCaseId = testCaseContext.getTestCaseId()   // 'Test Cases/TC1' for example
		GlobalVariable.CURRENT_TESTCASE_NAME = testCaseId.substring(testCaseId.indexOf('/') + 1)   // 'TC1' for example
		WebUI.comment("#beforeTestCase GlobalVariable.CURRENT_TESTCASE_NAME=${GlobalVariable.CURRENT_TESTCASE_NAME}")
		//
		openWorkbook()
	}

	/**
	 *
	 */
	@AfterTestCase
	def afterTestCase(TestCaseContext testCaseContext) {
		closeWorkbook()	
		WebUI.comment("#afterTestCase closed the workbook")
	}
	
	/**
	 *
	 */
	@AfterTestSuite
	def afterTestSuite(TestSuiteContext testSuiteContext) {
		// not necessary to close the workbook as it is closed by @AfterTestCase
		WebUI.comment("#afterTestSuite has nothing to do")
	}
	
}