package testcases;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import coreUtilities.testutils.ApiHelper;
import coreUtilities.utils.FileOperations;
import pages.StartupPage;
import pages.maternity_page;
import testBase.AppTestBase;
import testBase.UserActions;
import testdata.LocatorsFactory;

public class maternity_testcase extends AppTestBase {
	Map<String, String> configData;
	Map<String, String> loginCredentials;
	String expectedDataFilePath = testDataFilePath + "expected_data.xlsx";
	String loginFilePath = loginDataFilePath + "Login.xlsx";
	StartupPage startupPage;
	String randomInvoiceNumber;
	LocatorsFactory locatorsFactoryInstance;
	UserActions userActionsInstance;
	maternity_page maternity_pageInstance;

	@Parameters({ "browser", "environment" })
	@BeforeClass(alwaysRun = true)
	public void initBrowser(String browser, String environment) throws Exception {
		configData = new FileOperations().readExcelPOI(config_filePath, environment);
		configData.put("url", configData.get("url").replaceAll("[\\\\]", ""));
		configData.put("browser", browser);
		boolean isValidUrl = new ApiHelper().isValidUrl(configData.get("url"));
		Assert.assertTrue(isValidUrl,
				configData.get("url") + " might be Server down at this moment. Please try after sometime.");
		initialize(configData);
		startupPage = new StartupPage(driver);
	}

	@Test(priority = 1, groups = { "sanity" }, description = "1. Login in the healthapp application\r\n"
			+ "2. Scroll down menu till Maternity \r\n" + "3. Click on the Maternity"
			+ "4. Maternity module should be present ")
	public void verifyMaternityModule() throws Exception {
		maternity_pageInstance = new maternity_page(driver);
		Map<String, String> maternityExpectedData = new FileOperations().readExcelPOI(expectedDataFilePath,
				"maternityRecord");
		Map<String, String> loginData = new FileOperations().readExcelPOI(loginFilePath, "credentials");
		Assert.assertTrue(maternity_pageInstance.loginToHealthAppByGivenValidCredetial(loginData),
				"Login failed, Invalid credentials ! Please check manually");
		Assert.assertTrue(maternity_pageInstance.scrollDownAndClickMaternityTab());
		System.out.println("Verification Page url : " + maternityExpectedData.get("URL"));
		Assert.assertEquals(maternity_pageInstance.verifyMaternityPageUrl(), maternityExpectedData.get("URL"));
	}

	@Test(priority = 2, groups = {
			"sanity" }, description = "Pre condition: User should be logged in and it is on Maternity module\r\n"
					+ "1. Click on the Maternity  Module drop-down arrow")
	public void verifyMaternitySubModule() throws Exception {
		maternity_pageInstance = new maternity_page(driver);
		Assert.assertTrue(maternity_pageInstance.clickMaternityArrowAndVerifySubModules());
	}

	@Test(priority = 3, groups = {
			"sanity" }, description = "Pre condition: User should be logged in and it is on Maternity module \r\n"
					+ "1. Naviagte to the \"Maternity List\" sub-module\r\n"
					+ "2. Click on the \"Payments\" sub-module \r\n" + "3. Click on the \"Reports\" sub-module \r\n"
					+ "4. Naviaget back to the \"Maternity\" sub-module"
					+ "5. Ensure that it should  navigate to each sub-module which are present in the \"Maternity\" module")
	public void verifyNavigationBetweenMaternitySubModules() throws Exception {
		maternity_pageInstance = new maternity_page(driver);
		Assert.assertTrue(maternity_pageInstance.verifyNavigationBetweenMaternitySubModules());
	}

	@Test(priority = 4, groups = {
			"sanity" }, description = "Pre condition: User should be logged in and it is on Maternity module\r\n"
					+ "1. Navigate to \"Maternity list\" sub-module")
	public void verifyMaternityComponentsAreVisible() throws Exception {
		maternity_pageInstance = new maternity_page(driver);
		Assert.assertTrue(maternity_pageInstance.verifyMaternityComponentsAreVisible());
	}

	@Test(priority = 5, groups = {
			"sanity" }, description = "Pre condition: User should be logged in and it is on Maternity module\r\n"
					+ "1. Navigate to \"Maternity list\" sub-module\r\n"
					+ "2. Enter the Existing patient name in the \"Edit Information of\" field \r\n"
					+ "3. Select the patient name by the dropdown\r\n"
					+ "4. Enter the neccessay field in the \"New Patient Registration\" page\r\n"
					+ "(Husband's Name, Patient Height(in cm), Patient Weight(in kg), 1st dayOf Last Menstruation ,Expected Date of Delivery)\r\n"
					+ "5. Click on \"Register\" button")
	public void editPatientInformationAndVerify() throws Exception {
		maternity_pageInstance = new maternity_page(driver);
		Assert.assertTrue(maternity_pageInstance.editPatientInformationAndVerify());
	}

	@Test(priority = 6, groups = {
			"sanity" }, description = "Pre condition: User should be logged in and it is on Maternity module\r\n"
					+ "1. Click on the \"From\" date\r\n" + "2. Select the \"Jan 2020\" date \r\n"
					+ "3. Click on the \"To\" date\r\n" + "4. Select \"July 2024\" date\r\n"
					+ "5. Click on \"OK\" button")

	public void verifyToSearchDataApplyDateFilter() throws Exception {
		maternity_pageInstance = new maternity_page(driver);
		userActionsInstance = new UserActions(driver);

		userActionsInstance.click(maternity_pageInstance.getAnchorTagLocatorByText("Maternity List"));
		Assert.assertTrue(maternity_pageInstance.verifyUrlContains("PatientList"));
		Assert.assertTrue(maternity_pageInstance.applyDateFilter("01-01-2020", "01-01-2024"));
		Assert.assertTrue(maternity_pageInstance.verifyResultsAppointmentDateFallsWithin("01-01-2020", "01-01-2024"));
	}

	@Test(priority = 7, groups = {
			"sanity" }, description = "Pre condition: User should be logged in and it is on Maternity module\r\n"
					+ "1. Navigate to \"Maternity list\" sub-module\r\n" + "2. Click on the data range button\r\n"
					+ "3. select \"one week\" option from the drop down\r\n" + "4. Click on \"OK\" button")
	public void verifyDateRangeUsingDateRangeButton() throws Exception {
		maternity_pageInstance = new maternity_page(driver);

		Assert.assertTrue(maternity_pageInstance.clickDateRangeDropdownAndSelect("Last 1 Week"));
		LocalDate currentDate = LocalDate.now();
		LocalDate date7DaysAgo = currentDate.minusDays(7);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		String toDate = currentDate.format(formatter);
		String fromDate = date7DaysAgo.format(formatter);
		Thread.sleep(3000);
		Assert.assertTrue(maternity_pageInstance.verifyResultsAppointmentDateFallsWithin(fromDate, toDate));
	}

	@Test(priority = 8, groups = {
			"sanity" }, description = "Pre condition: User should be logged in and  it is on Maternity module\r\n"
					+ "1. Navigate to \"Maternity list\" sub-module \r\n"
					+ "2. Clcik on the \"View All Maternity Patients\" checkbox")
	public void verifyViewAllMaternityPatientCheckBoxFunctionality() throws Exception {
		maternity_pageInstance = new maternity_page(driver);

		Assert.assertTrue(maternity_pageInstance.verifyViewAllMaternityPatientCheckBoxFunctionality());
	}

	@Test(priority = 9, groups = {
			"sanity" }, description = "Pre condition: User should be logged in and  it is on Maternity module\r\n"
					+ "1. Navigate to \"Maternity list\" sub-module \r\n" + "2. Enter the keywords")
	public void searchAndVerifyKeywordInEveryResult() throws Exception {
		maternity_pageInstance = new maternity_page(driver);
		Assert.assertTrue(maternity_pageInstance.searchAndVerifyKeywordInEveryResult("2024-08-09"));
	}

	@Test(priority = 10, groups = {
			"sanity" }, description = "Pre condition: User should be logged in and it is on Maternity module\r\n"
					+ "1. Navigate to \"Maternity list\" sub-module \r\n" + "2. Hover the mouse in start")

	public void verifyTooltipAndPresentationOnMouseHoverStar() throws Exception {
		maternity_pageInstance = new maternity_page(driver);
		Map<String, String> maternityRecordExpectedData = new FileOperations().readExcelPOI(expectedDataFilePath,
				"maternityRecord");
		Assert.assertEquals(maternity_pageInstance.verifyToolTipText(),
				maternityRecordExpectedData.get("favouriteIcon"));
	}

	@Test(priority = 11, groups = {
			"sanity" }, description = "Pre condition: User should be logged in and it is on Maternity module\r\n"
					+ "1. Navigate to \"Maternity list\" sub-module \r\n"
					+ "2. Click on the \"View\" button of a specific records\r\n"
					+ "3. Click on \" Updated Patient\" button"
					+ "4. Verify that a success message, \"Patient details updated successfully,\" appears.")

	public void verifyViewbuttonFunctionality() throws Exception {
		maternity_pageInstance = new maternity_page(driver);
		Map<String, String> maternityRecordExpectedData = new FileOperations().readExcelPOI(expectedDataFilePath,
				"maternityRecord");
		Assert.assertEquals(maternity_pageInstance.verifyViewbuttonFunctionality(),
				maternityRecordExpectedData.get("detailsUpdateMsg"));
	}

	@Test(priority = 12, groups = {
			"sanity" }, description = "Pre condition: User should be logged in and it is on Maternity module\r\n"
					+ "1. Navigate to \"Maternity list\" sub-module \r\n"
					+ "2. Click on the \"ANC\" button of a specific records\r\n"
					+ "3. Click on \" Updated Patient\" button")

	public void verifyANCbuttonFunctionality() throws Exception {
		maternity_pageInstance = new maternity_page(driver);
		Map<String, String> maternityRecordExpectedData = new FileOperations().readExcelPOI(expectedDataFilePath,
				"maternityRecord");
		Assert.assertEquals(maternity_pageInstance.verifyANCbuttonFunctionality(),
				maternityRecordExpectedData.get("ancUpdatedMsg"));
	}

	@Test(priority = 13, groups = {
			"sanity" }, description = "Pre condition: User should be logged in and it is on Maternity module\r\n"
					+ "1. Navigate to \"Maternity list\" sub-module \r\n"
					+ "2. Click on the \"...\" button of a specific records\r\n"
					+ "3. Click on \" Mat - Register\" option from a specfic record\r\n"
					+ "4. Fill all necessary field present in the \"Maternity Register\"  page\r\n"
					+ "5. Click on \"Save\" button")

	public void verifyMatRegisterbuttonFunctionality() throws Exception {
		maternity_pageInstance = new maternity_page(driver);
		Map<String, String> maternityRecordExpectedData = new FileOperations().readExcelPOI(expectedDataFilePath,
				"maternityRecord");
		Assert.assertEquals(maternity_pageInstance.verifyMatRegisterbuttonFunctionality(),
				maternityRecordExpectedData.get("matRegisterMsg"));
	}

	@Test(priority = 14, groups = {
			"sanity" }, description = "Pre condition: User should be logged in and it is on Maternity module\r\n"
					+ "1. Navigate to \"Maternity list\" sub-module \r\n"
					+ "2. Click on the \"...\" button of a specific records\r\n"
					+ "3. Click on \" Conclude\" option from a specfic record\r\n"
					+ "4. Click on the \"Confirm\" button "
					+ "5. Ensure that after clicking the \"Conclude\" option, an alert box appears."
					+ "6. Verify that upon clicking \"Confirm\" in the alert box, a success message \"Successfully Concluded.\" is displayed.")

	public void verifyConcludeButtonFunctionality() throws Exception {
		maternity_pageInstance = new maternity_page(driver);
		Map<String, String> maternityRecordExpectedData = new FileOperations().readExcelPOI(expectedDataFilePath,
				"maternityRecord");
		Assert.assertEquals(maternity_pageInstance.verifyConcludeButtonFunctionality(),
				maternityRecordExpectedData.get("concludedMsg"));
	}

	@Test(priority = 15, groups = {
			"sanity" }, description = "Pre condition: User should be logged in and it is on Maternity module\r\n"
					+ "1. Navigate to \"Maternity list\" sub-module \r\n"
					+ "2. Click on the \"...\" button of a specific records\r\n"
					+ "3. Click on \"Remove\" option from a specfic record\r\n" + "4. Click on the \"Remove\" button "
					+ "5. Ensure that after clicking the \"Remove\" option, an alert box appears."
					+ "6. Verify that upon clicking \"Remove\" in the alert box, a success message \"Successfully Removed..\" is displayed.")

	public void verifyRemoveButtonFunctionality() throws Exception {
		maternity_pageInstance = new maternity_page(driver);
		Map<String, String> maternityRecordExpectedData = new FileOperations().readExcelPOI(expectedDataFilePath,
				"maternityRecord");
		Assert.assertEquals(maternity_pageInstance.verifyRemoveButtonFunctionality(),
				maternityRecordExpectedData.get("removedMsg"));
	}

	@Test(priority = 16, groups = {
			"sanity" }, description = "Pre condition: User should be logged in and it is on Maternity module\r\n"
					+ "1. Navigate to the \"Maternity List\" sub module\r\n"
					+ "2. Stretch the column by Click on the line present between 2 columns")
	public void verifyColumnsAreStretchable() throws Exception {
		maternity_pageInstance = new maternity_page(driver);
		Assert.assertTrue(maternity_pageInstance.verifyColumnsAreStretchableFromRight("Address"));
		Assert.assertTrue(maternity_pageInstance.verifyColumnsAreStretchableFromRight("HusbandName"));
	}

	@Test(priority = 17, groups = {
			"sanity" }, description = "Pre-condition: user should be logged in and it is on Maternity module\r\n"
					+ "1. Navigate to the \"Maternity List\" sub-module\r\n"
					+ "2. Select \"Jan 2024\" from the \"From\" field \r\n"
					+ "3. Select \"August 2024\" from the \"To\" field \r\n"
					+ "4. Scroll  all the way to the botton of the page \r\n" + "5. Click on \"Next\" button")
	public void verifyPaginationFunctionality() throws Exception {
		maternity_pageInstance = new maternity_page(driver);
		Assert.assertTrue(maternity_pageInstance.verifyPaginationFunctionality());
	}

	@Test(priority = 18, groups = {
			"sanity" }, description = "Pre condition: User should be logged in and it is on Maternity module\r\n"
					+ "1. Navigate to \"Payments\" sub-module")
	public void verifyPaymentsComponentsAreVisible() throws Exception {
		maternity_pageInstance = new maternity_page(driver);
		Assert.assertTrue(maternity_pageInstance.verifyPaymentsComponentsAreVisible());
	}

	@Test(priority = 19, groups = {
			"sanity" }, description = "Pre condition: User should be logged in and it is on Maternity module\r\n"
					+ "1. Navigate to \"Payments\" sub-module\r\n"
					+ "2. Click on the \"Search from All Patient\" checkbox")
	public void verifySearchFromAllPatientCheckboxFunctionality() throws Exception {
		maternity_pageInstance = new maternity_page(driver);
		Assert.assertTrue(maternity_pageInstance.verifySearchFromAllPatientCheckboxFunctionality());
	}

	@Test(priority = 20, groups = {
			"sanity" }, description = "Pre condition: User should be logged in and it is on Maternity module\r\n"
					+ "1. Navigate to \"Reports\" sub-module\r\n"
					+ "2. Click on \"Maternity Allowance Reports\" sub-modal")
	public void verifyReportsComponentsAreVisible() throws Exception {
		maternity_pageInstance = new maternity_page(driver);
		Assert.assertTrue(maternity_pageInstance.verifyReportsComponentsAreVisible());
	}

	@Test(priority = 21, groups = {
			"sanity" }, description = "Pre condition: User should be logged in and must be on the \"Maternity\" module\r\n"
					+ "1. Navigate to the \"Report\" sub-modules\r\n" + "2. Click on the\" show Report\" button\r\n"
					+ "3. Click on the \"Export\" button"
					+ "4. Ensure that the exported file is in the correct format and contains the data as displayed in the report.\r\n")

	public void verifyExportButtonFunctionality() throws Exception {
		maternity_pageInstance = new maternity_page(driver);

		Assert.assertTrue(maternity_pageInstance.verifyFileDownloaded("InpatientServiceReport"));
	}

	@AfterClass(alwaysRun = true)
	public void tearDown() {
		System.out.println("before closing the browser");
		browserTearDown();
	}

	@AfterMethod
	public void retryIfTestFails() throws Exception {
		startupPage.navigateToUrl(configData.get("url"));
	}
}
