package org.jembi.bsis.controller;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.jembi.bsis.controllerservice.ReportsControllerService;
import org.jembi.bsis.model.inventory.InventoryStatus;
import org.jembi.bsis.model.reporting.Report;
import org.jembi.bsis.service.report.BloodUnitsIssuedReportGenerator;
import org.jembi.bsis.service.report.CollectedDonationsReportGenerator;
import org.jembi.bsis.service.report.ComponentProductionReportGenerator;
import org.jembi.bsis.service.report.DiscardedComponentReportGenerator;
import org.jembi.bsis.service.report.DonorsAdverseEventsReportGenerator;
import org.jembi.bsis.service.report.DonorsDeferredSummaryReportGenerator;
import org.jembi.bsis.service.report.StockLevelsReportGenerator;
import org.jembi.bsis.service.report.TransfusionSummaryReportGenerator;
import org.jembi.bsis.service.report.TtiPrevalenceReportGenerator;
import org.jembi.bsis.utils.PermissionConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("reports")
public class ReportsController {

	@Autowired
	private StockLevelsReportGenerator stockLevelsReportGenerator;

	@Autowired
	private TtiPrevalenceReportGenerator ttiPrevalenceReportGenerator;

	@Autowired
	private BloodUnitsIssuedReportGenerator bloodUnitsIssuedReportGenerator;

	@Autowired
	private DonorsDeferredSummaryReportGenerator donorsDeferredSummaryReportGenerator;

	@Autowired
	private CollectedDonationsReportGenerator collectedDonationsReportGenerator;

	@Autowired
	private DonorsAdverseEventsReportGenerator donorsAdverseEventsReportGenerator;

	@Autowired
	private ReportsControllerService reportsControllerService;

	@Autowired
	private DiscardedComponentReportGenerator discardedComponentReportGenerator;

	@Autowired
	private ComponentProductionReportGenerator componentProductionReportGenerator;

	@Autowired
	private TransfusionSummaryReportGenerator transfusionSummaryReportGenerator;

	@RequestMapping(value = "/transfusionsummary/form", method = RequestMethod.GET)
	@PreAuthorize("hasRole('" + PermissionConstants.TRANSFUSIONS_REPORTING + "')")
	public Map<String, Object> transfusionSummaryFormFields() {
		Map<String, Object> map = new HashMap<>();
		map.put("usageSites", reportsControllerService.getUsageSites());
		map.put("transfusionReactionTypes", reportsControllerService.getTransfusionReactionTypes());
		return map;
	}

	@RequestMapping(value = "/transfusionsummary/generate", method = RequestMethod.GET)
	@PreAuthorize("hasRole('" + PermissionConstants.TRANSFUSIONS_REPORTING + "')")
	public Report generateTransfusionSummaryReport(
			@RequestParam(value = "transfusionSiteId", required = false) UUID transfusionSiteId,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startDate,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endDate) {
		return transfusionSummaryReportGenerator.generateTransfusionSummaryReport(transfusionSiteId, startDate,
				endDate);
	}

	@RequestMapping(value = "/discardedunits/form", method = RequestMethod.GET)
	@PreAuthorize("hasRole('" + PermissionConstants.COMPONENTS_REPORTING + "')")
	public Map<String, Object> discardedUnitsFormFields() {
		Map<String, Object> map = new HashMap<>();
		map.put("processingSites", reportsControllerService.getProcessingSites());
		map.put("componentTypes", reportsControllerService.getAllComponentTypes());
		map.put("discardReasons", reportsControllerService.getAllDiscardReasons(false));
		return map;
	}

	@RequestMapping(value = "/discardedunits/generate", method = RequestMethod.GET)
	@PreAuthorize("hasRole('" + PermissionConstants.COMPONENTS_REPORTING + "')")
	public Report generateDiscardedUnits(
			@RequestParam(value = "processingSite", required = false) UUID processingSiteId,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startDate,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endDate) {
		return discardedComponentReportGenerator.generateDiscardedComponents(processingSiteId, startDate, endDate);
	}

	@RequestMapping(value = "/stockLevels/generate", method = RequestMethod.GET)
	@PreAuthorize("hasRole('" + PermissionConstants.VIEW_INVENTORY_INFORMATION + "')")
	public Report findStockLevels(@RequestParam(value = "location", required = false) UUID locationId,
			@RequestParam(value = "inventoryStatus", required = true) InventoryStatus inventoryStatus) {
		return stockLevelsReportGenerator.generateStockLevelsForLocationReport(locationId, inventoryStatus);
	}

	@RequestMapping(value = "/stockLevels/form", method = RequestMethod.GET)
	@PreAuthorize("hasRole('" + PermissionConstants.VIEW_INVENTORY_INFORMATION + "')")
	public Map<String, Object> stockLevelsFormGenerator() {
		Map<String, Object> map = new HashMap<>();
		map.put("distributionSites", reportsControllerService.getDistributionSites());
		return map;
	}

	@RequestMapping(value = "/collecteddonations/generate", method = RequestMethod.GET)
	@PreAuthorize("hasRole('" + PermissionConstants.DONATIONS_REPORTING + "')")
	public Report getCollectedDonationsReport(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startDate,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endDate) {
		return collectedDonationsReportGenerator.generateCollectedDonationsReport(startDate, endDate);
	}

	@RequestMapping(value = "/collecteddonations/form", method = RequestMethod.GET)
	@PreAuthorize("hasRole('" + PermissionConstants.DONATIONS_REPORTING + "')")
	public Map<String, Object> getCollectedDonationsReportFormFields() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("donationTypes", reportsControllerService.getDonationTypes());
		return map;
	}

	@RequestMapping(value = "/ttiprevalence/form", method = RequestMethod.GET)
	@PreAuthorize("hasRole('" + PermissionConstants.TTI_REPORTING + "')")
	public Map<String, Object> getActiveTTIBloodTestsReportForm() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ttiBloodTests", reportsControllerService.getEnabledTTIBloodTests());
		return map;
	}

	@RequestMapping(value = "/ttiprevalence/generate", method = RequestMethod.GET)
	@PreAuthorize("hasRole('" + PermissionConstants.TTI_REPORTING + "')")
	public Report getTTIPrevalenceReport(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startDate,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endDate) {
		return ttiPrevalenceReportGenerator.generateTTIPrevalenceReport(startDate, endDate);
	}

	@RequestMapping(value = "/unitsissued/form", method = RequestMethod.GET)
	@PreAuthorize("hasRole('" + PermissionConstants.COMPONENTS_REPORTING + "')")
	public Map<String, Object> getUnitsIssuedReportFormFields() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("componentTypes", reportsControllerService.getAllComponentTypesThatCanBeIssued());
		return map;
	}

	@RequestMapping(value = "/unitsissued/generate", method = RequestMethod.GET)
	@PreAuthorize("hasRole('" + PermissionConstants.COMPONENTS_REPORTING + "')")
	public Report generateUnitsIssuedReport(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startDate,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endDate) {
		return bloodUnitsIssuedReportGenerator.generateUnitsIssuedReport(startDate, endDate);
	}

	@RequestMapping(value = "/donorsdeferred/form", method = RequestMethod.GET)
	@PreAuthorize("hasRole('" + PermissionConstants.DONORS_REPORTING + "')")
	public Map<String, Object> generateDonorsDeferredFormFields() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("deferralReasons", reportsControllerService.getDeferralReasons());
		return map;
	}

	@RequestMapping(value = "/donorsdeferred/generate", method = RequestMethod.GET)
	@PreAuthorize("hasRole('" + PermissionConstants.DONORS_REPORTING + "')")
	public Report generateDonorsDeferredReport(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startDate,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endDate) {
		return donorsDeferredSummaryReportGenerator.generateDonorDeferralSummaryReport(startDate, endDate);
	}

	@RequestMapping(value = "/donorsadverseevents/form", method = RequestMethod.GET)
	@PreAuthorize("hasRole('" + PermissionConstants.DONATIONS_REPORTING + "')")
	public Map<String, Object> generateDonorsAdverseEventsForm() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("venues", reportsControllerService.getVenues());
		map.put("adverseEventTypes", reportsControllerService.getAdverseEventTypes());
		return map;
	}

	@RequestMapping(value = "/donorsadverseevents/generate", method = RequestMethod.GET)
	@PreAuthorize("hasRole('" + PermissionConstants.DONATIONS_REPORTING + "')")
	public Report generateDonorsAdverseEventsReport(@RequestParam(value = "venue", required = false) UUID venueId,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startDate,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endDate) {
		return donorsAdverseEventsReportGenerator.generateDonorsAdverseEventsReport(venueId, startDate, endDate);
	}

	@RequestMapping(value = "/componentsprocessed/form", method = RequestMethod.GET)
	@PreAuthorize("hasRole('" + PermissionConstants.COMPONENTS_REPORTING + "')")
	public Map<String, Object> getProcessingSitesAndComponentTypes() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("processingSites", reportsControllerService.getProcessingSites());
		map.put("componentTypes", reportsControllerService.getAllComponentTypesThatCanBeIssued());
		return map;
	}

	@RequestMapping(value = "/componentsprocessed/generate", method = RequestMethod.GET)
	@PreAuthorize("hasRole('" + PermissionConstants.COMPONENTS_REPORTING + "')")
	public Report generateComponentProductionReport(
			@RequestParam(value = "processingSite", required = false) UUID processingSiteId,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startDate,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endDate) {
		return componentProductionReportGenerator.generateComponentProductionReport(processingSiteId, startDate,
				endDate);
	}

	// added by kaleb
	@RequestMapping(value = "/whoreport/generate", method = RequestMethod.GET)
	@PreAuthorize("hasRole('" + PermissionConstants.DONATIONS_REPORTING + "')")
	public Map<String, Object> getWHOreport(@RequestParam String startDate, @RequestParam String endDate)
			throws SQLException {
		Map<String, Object> map = new HashMap<String, Object>();

		String sql1 = "select count(*) from donor where donor.isDeleted=false and donor.createdDate>'" + startDate
				+ "' and donor.createdDate<'" + endDate + "';";
		map.put("totaldonors", reportsControllerService.doWHOQueries(sql1));

		String sql2 = "SELECT count(*)  FROM donation where  donation.isDeleted=false and donation.donationType_id=1 and donation.donationDate>'"
				+ startDate + "' and donation.donationDate<'" + endDate + "';";

		map.put("voluntarydonations", reportsControllerService.doWHOQueries(sql2));

		//

		String sql3 = "SELECT count(*) FROM donordeferral,deferralreason where donordeferral.isVoided=false and donordeferral.deferralReason_id=deferralreason.id and deferralreason.durationType='PERMANENT' and donordeferral.deferralDate>'"
				+ startDate + "' and donordeferral.deferralDate<'" + endDate + "';";

		map.put("permanentdeferral", reportsControllerService.doWHOQueries(sql3));
		//

		String sql4 = "SELECT count(*) FROM donordeferral,deferralreason where donordeferral.isVoided=false and donordeferral.deferralReason_id=deferralreason.id and deferralreason.durationType='TEMPORARY' and donordeferral.deferralDate>'"
				+ startDate + "' and donordeferral.deferralDate<'" + endDate + "';";

		map.put("temporarydeferral", reportsControllerService.doWHOQueries(sql4));

		//

		String sql5 = "SELECT count(*) FROM donordeferral,deferralreason where donordeferral.isVoided=false and donordeferral.deferralReason_id=deferralreason.id and deferralreason.id=3 and donordeferral.deferralDate>'"
				+ startDate + "' and donordeferral.deferralDate<'" + endDate + "';";

		map.put("lowweight", reportsControllerService.doWHOQueries(sql5));
		//

		String sql6 = "SELECT count(*) FROM donordeferral,deferralreason where donordeferral.isVoided=false and donordeferral.deferralReason_id=deferralreason.id and deferralreason.id=4 and donordeferral.deferralDate>'"
				+ startDate + "' and donordeferral.deferralDate<'" + endDate + "';";

		map.put("lowhaemoglobin", reportsControllerService.doWHOQueries(sql6));
		//

		String sql7 = "SELECT count(*) FROM donordeferral,deferralreason where donordeferral.isVoided=false and donordeferral.deferralReason_id=deferralreason.id and deferralreason.id=6 and donordeferral.deferralDate>'"
				+ startDate + "' and donordeferral.deferralDate<'" + endDate + "';";

		map.put("medicalcondition", reportsControllerService.doWHOQueries(sql7));
		//

		String sql8 = "SELECT count(*) FROM donordeferral,deferralreason where donordeferral.isVoided=false and donordeferral.deferralReason_id=deferralreason.id and deferralreason.id=2 and donordeferral.deferralDate>'"
				+ startDate + "' and donordeferral.deferralDate<'" + endDate + "';";

		map.put("highriskbehaviour", reportsControllerService.doWHOQueries(sql8));
		//

		String sql9 = "SELECT count(*) FROM donordeferral,deferralreason where donordeferral.isVoided=false and donordeferral.deferralReason_id=deferralreason.id and deferralreason.id=7 and donordeferral.deferralDate>'"
				+ startDate + "' and donordeferral.deferralDate<'" + endDate + "';";

		map.put("travelhistory", reportsControllerService.doWHOQueries(sql9));
		//

		String sql10 = "SELECT count(*) FROM donordeferral,deferralreason where donordeferral.isVoided=false and donordeferral.deferralReason_id=deferralreason.id and deferralreason.id=1 and donordeferral.deferralDate>'"
				+ startDate + "' and donordeferral.deferralDate<'" + endDate + "';";

		map.put("testoutcomes", reportsControllerService.doWHOQueries(sql10));
		//

		String sql11 = "SELECT count(*) FROM donordeferral,deferralreason where donordeferral.isVoided=false and donordeferral.deferralReason_id=deferralreason.id and deferralreason.id=5 and donordeferral.deferralDate>'"
				+ startDate + "' and donordeferral.deferralDate<'" + endDate + "';";

		map.put("otherreasons", reportsControllerService.doWHOQueries(sql11));

		map.put("totaldeferrals",
				reportsControllerService.doWHOQueries(sql5) + reportsControllerService.doWHOQueries(sql6)
						+ reportsControllerService.doWHOQueries(sql7) + reportsControllerService.doWHOQueries(sql8)
						+ reportsControllerService.doWHOQueries(sql9) + reportsControllerService.doWHOQueries(sql10)
						+ reportsControllerService.doWHOQueries(sql11));

		//
		String sql13 = "select count(*) from donor, donation where donor.isDeleted=false and donation.isDeleted=false and donation.donor_id=donor.id and donor.gender='male' and donation.donationDate>'"
				+ startDate + "' and donation.donationDate<'" + endDate + "';";

		map.put("maledonations", reportsControllerService.doWHOQueries(sql13));

		//
		String sql14 = "select count(*) from donor, donation where donor.isDeleted=false and donation.isDeleted=false and donation.donor_id=donor.id and donor.gender='female' and donation.donationDate>'"
				+ startDate + "' and donation.donationDate<'" + endDate + "';";

		map.put("femaledonations", reportsControllerService.doWHOQueries(sql14));

		//
		String sql15 = "select count(*) from donor, donation where donor.isDeleted=false and donation.isDeleted=false and donation.donor_id=donor.id and year(Now())-year(donor.birthDate)<18 and donation.donationDate>'"
				+ startDate + "' and donation.donationDate<'" + endDate + "';";

		map.put("under18donations", reportsControllerService.doWHOQueries(sql15));

		//
		String sql16 = "select count(*) from donor, donation where donor.isDeleted=false and donation.isDeleted=false and donation.donor_id=donor.id and year(Now())-year(donor.birthDate)>=18 and year(Now())-year(donor.birthDate)<24 and donation.donationDate>'"
				+ startDate + "' and donation.donationDate<'" + endDate + "';";

		map.put("under24donations", reportsControllerService.doWHOQueries(sql16));

		//
		String sql17 = "select count(*) from donor, donation where donor.isDeleted=false and donation.isDeleted=false and donation.donor_id=donor.id and year(Now())-year(donor.birthDate)>=24 and year(Now())-year(donor.birthDate)<44 and donation.donationDate>'"
				+ startDate + "' and donation.donationDate<'" + endDate + "';";

		map.put("under44donations", reportsControllerService.doWHOQueries(sql17));

		//
		String sql18 = "select count(*) from donor, donation where donor.isDeleted=false and donation.isDeleted=false and donation.donor_id=donor.id and year(Now())-year(donor.birthDate)>=44 and year(Now())-year(donor.birthDate)<64 and donation.donationDate>'"
				+ startDate + "' and donation.donationDate<'" + endDate + "';";

		map.put("under64donations", reportsControllerService.doWHOQueries(sql18));

		//
		String sql19 = "select count(*) from donor, donation where donor.isDeleted=false and donation.isDeleted=false and donation.donor_id=donor.id and year(Now())-year(donor.birthDate)>=64 and donation.donationDate>'"
				+ startDate + "' and donation.donationDate<'" + endDate + "';";

		map.put("over64donations", reportsControllerService.doWHOQueries(sql19));

		return map;
	}

	@RequestMapping(value = "/donationreport/generate", method = RequestMethod.GET)
	@PreAuthorize("hasRole('" + PermissionConstants.DONATIONS_REPORTING + "')")
	public Map<String, Object> getDonationreport(@RequestParam String startDate, @RequestParam String endDate)
			throws SQLException {
		Map<String, Object> map = new HashMap<String, Object>();
		String sql = "SELECT r.donorNumber,r.firstName,r.middleName,r.lastName, " + "(" + "	select count(*) "
				+ "	from donation a" + "    where a.donor_id=r.id" + " and a.donationDate>'" + startDate + "'"
				+ " and a.donationDate<='" + endDate + "'" + ")" + " as donation_number " + "from donor r;";
		

		map.put("donationreport", reportsControllerService.doDonorQueries(sql));
		return map;
	}

	// end of added by kaleb
}
