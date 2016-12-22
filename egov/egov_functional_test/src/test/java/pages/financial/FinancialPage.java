package pages.financial;

import entities.ptis.ApprovalDetails;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;
import pages.BasePage;

import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * Created by vinaykumar on 20/12/16.
 */
public class FinancialPage extends BasePage {

    private WebDriver webDriver;

    @FindBy(id = "vType")
    private WebElement voucherSubType;

    @FindBy(id = "fundId")
    private WebElement fundId;

    @FindBy(id = "vouchermis.departmentid")
    private WebElement voucherDepartment;

    @FindBy(id = "vouchermis.function")
    private WebElement voucherFunction;

    @FindBy(id = "billDetailslist[0].glcodeDetail")
    private WebElement accountCode1;

    @FindBy(id = "billDetailslist[1].glcodeDetail")
    private WebElement accountCode2;

    @FindBy(id = "billDetailslist[0].debitAmountDetail")
    private WebElement debitAmount1;

    @FindBy(id = "billDetailslist[1].creditAmountDetail")
    private WebElement creditAmount2;

    @FindBy(id = "totalcramount")
    private WebElement totalCreditAmount;

    @FindBy(id = "subLedgerlist[0].glcode.id")
    private WebElement ledgerAccount1;

    @FindBy(id = "subLedgerlist[1].glcode.id")
    private WebElement ledgerAccount2;

    @FindBy(id = "subLedgerlist[0].detailType.id")
    private WebElement ledgerType1;

    @FindBy(id = "subLedgerlist[1].detailType.id")
    private WebElement ledgerType2;

    @FindBy(id = "subLedgerlist[0].detailCode")
    private WebElement ledgerCode1;

    @FindBy(id = "subLedgerlist[1].detailCode")
    private WebElement ledgerCode2;

    @FindBy(id = "approverDepartment")
    private WebElement approverDepartment;

    @FindBy(id = "approverDesignation")
    private WebElement approverDesignation;

    @FindBy(id = "approverPositionId")
    private WebElement approverPosition;

    @FindBy(id = "Forward")
    private WebElement forwardButton;

    @FindBy(id = "subLedgerlist[0].amount")
    private WebElement ledgerAmount1;

    @FindBy(id = "subLedgerlist[1].amount")
    private WebElement ledgerAmount2;

    @FindBy(xpath = ".//*[@id='egov_yui_add_image']")
    private List<WebElement> addList;

    @FindBy(className = "btn btn-primary")
    private WebElement okButton;

    @FindBy(id = "button2")
    private WebElement closeButton;

    public void enterJournalVoucherDetails(String voucherType , String accountCode){

        new Select(voucherSubType).selectByVisibleText(voucherType);
        new Select(fundId).selectByVisibleText("Municipal Fund");
        new Select(voucherDepartment).selectByVisibleText("PUBLIC HEALTH AND SANITATION");
        new Select(voucherFunction).selectByVisibleText("Public Health");

        accountCode1.sendKeys(accountCode.split("\\_")[0],  Keys.TAB);
        enterText(debitAmount1 , "100");

        accountCode2.sendKeys(accountCode.split("\\_")[1] , Keys.TAB);
        enterText(creditAmount2 , "100");

        new Select(ledgerAccount1).selectByVisibleText(accountCode.split("\\_")[0]);
        new Select(ledgerType1).selectByVisibleText("contractor");
        ledgerCode1.sendKeys("KMC001");

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ledgerCode1.sendKeys(Keys.ENTER);
        ledgerAmount1.sendKeys("100");

        addList.get(2).click();

        new Select(ledgerAccount2).selectByVisibleText(accountCode.split("\\_")[1]);
        new Select(ledgerType2).selectByVisibleText("contractor");
        ledgerCode2.sendKeys("KMC001");

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ledgerCode2.sendKeys(Keys.ENTER);
        ledgerAmount2.sendKeys("100");
    }

    public void enterFinanceApprovalDetails(ApprovalDetails approvalDetails){

        new Select(approverDepartment).selectByVisibleText(approvalDetails.getApproverDepartment());
        new Select(approverDesignation).selectByVisibleText(approvalDetails.getApproverDesignation());
        new Select(approverPosition).selectByVisibleText(approvalDetails.getApprover());

        forwardButton.click();
        //switchToNewlyOpenedWindow(webDriver);
        webDriver.switchTo().alert().accept();
        okButton.click();
        closeButton.click();

    }

}