package org.jembi.bsis.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.validation.Valid;

import org.jembi.bsis.backingform.ReturnFormBackingForm;
import org.jembi.bsis.backingform.validator.ReturnFormBackingFormValidator;
import org.jembi.bsis.controllerservice.DeferralControllerService;
import org.jembi.bsis.controllerservice.ReturnFormControllerService;
import org.jembi.bsis.factory.DonationBatchViewModelFactory;
import org.jembi.bsis.factory.DonationTypeFactory;
import org.jembi.bsis.factory.LocationFactory;
import org.jembi.bsis.factory.PackTypeFactory;
import org.jembi.bsis.model.donationbatch.DonationBatch;
import org.jembi.bsis.model.donationtype.DonationType;
import org.jembi.bsis.model.location.Location;
import org.jembi.bsis.model.returnform.ReturnStatus;
import org.jembi.bsis.model.user.User;
import org.jembi.bsis.repository.AdverseEventTypeRepository;
import org.jembi.bsis.repository.ContactMethodTypeRepository;
import org.jembi.bsis.repository.DonationBatchRepository;
import org.jembi.bsis.repository.DonationTypeRepository;
import org.jembi.bsis.repository.DonorRepository;
import org.jembi.bsis.repository.LocationRepository;
import org.jembi.bsis.repository.PackTypeRepository;
import org.jembi.bsis.utils.CustomDateFormatter;
import org.jembi.bsis.utils.PermissionConstants;
import org.jembi.bsis.viewmodel.DonationBatchViewModel;
import org.jembi.bsis.viewmodel.LocationFullViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("returnforms")
public class ReturnFormController {

	@Autowired
	private ReturnFormControllerService returnFormControllerService;

	@Autowired
	private ReturnFormBackingFormValidator validator;

	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private LocationFactory locationFactory;

	// added by kaleb

	@Autowired
	private DonationTypeRepository donorTypeRepository;

	@Autowired
	private DonationTypeFactory donationTypeFactory;

	@Autowired
	private PackTypeRepository packTypeRepository;

	@Autowired
	private PackTypeFactory packTypeFactory;

	@Autowired
	private AdverseEventTypeRepository adverseEventTypeRepository;

	@Autowired
	private DonationBatchRepository donationBatchRepository;

	@Autowired
	private DonationBatchViewModelFactory donationBatchViewModelFactory;

	@Autowired
	private DonorRepository donorRepository;

	@Autowired
	private DeferralControllerService deferralControllerService;

	@Autowired
	private ContactMethodTypeRepository contactMethodTypeRepository;

	// end of added by kaleb

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		binder.setValidator(validator);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/form")
	@PreAuthorize("hasRole('" + PermissionConstants.ADD_ORDER_FORM + "')")
	public ResponseEntity<Map<String, Object>> getOrderFormForm() {
		List<Location> usageSites = locationRepository.getUsageSites();
		List<Location> distributionSites = locationRepository.getDistributionSites();

		Map<String, Object> map = new HashMap<>();
		map.put("returnForm", new ReturnFormBackingForm());
		map.put("usageSites", locationFactory.createFullViewModels(usageSites));
		map.put("distributionSites", locationFactory.createFullViewModels(distributionSites));
		return new ResponseEntity<>(map, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST)
	@PreAuthorize("hasRole('" + PermissionConstants.ADD_ORDER_FORM + "')")
	public ResponseEntity<Map<String, Object>> addReturnForm(@Valid @RequestBody ReturnFormBackingForm backingForm) {
		Map<String, Object> map = new HashMap<>();
		map.put("returnForm", returnFormControllerService.createReturnForm(backingForm));
		return new ResponseEntity<>(map, HttpStatus.CREATED);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
	@PreAuthorize("hasRole('" + PermissionConstants.VIEW_ORDER_FORM + "')")
	public ResponseEntity<Map<String, Object>> getReturnForm(@PathVariable UUID id) {
		Map<String, Object> map = new HashMap<>();
		map.put("returnForm", returnFormControllerService.findById(id));
		return new ResponseEntity<>(map, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/{id}")
	@PreAuthorize("hasRole('" + PermissionConstants.EDIT_ORDER_FORM + "')")
	public ResponseEntity<Map<String, Object>> updateReturnForm(@PathVariable("id") UUID id,
			@Valid @RequestBody ReturnFormBackingForm backingForm) {

		// Use the id parameter from the path
		backingForm.setId(id);

		Map<String, Object> map = new HashMap<>();
		map.put("returnForm", returnFormControllerService.updateReturnForm(backingForm));
		return new ResponseEntity<>(map, HttpStatus.OK);
	}

	@RequestMapping(value = "/search", method = RequestMethod.GET)
	@PreAuthorize("hasRole('" + PermissionConstants.VIEW_ORDER_FORM + "')")
	public ResponseEntity<Map<String, Object>> findReturnForms(
			@RequestParam(value = "returnDateFrom", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date returnDateFrom,
			@RequestParam(value = "returnDateTo", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date returnDateTo,
			@RequestParam(value = "returnedFromId", required = false) UUID returnedFromId,
			@RequestParam(value = "returnedToId", required = false) UUID returnedToId,
			@RequestParam(value = "status", required = false) ReturnStatus status) {

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("returnForms", returnFormControllerService.findReturnForms(returnDateFrom, returnDateTo, returnedFromId,
				returnedToId, status));

		return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("hasRole('" + PermissionConstants.VOID_ORDER_FORM + "')")
	public void deleteReturnForm(@PathVariable("id") UUID returnFormId) {
		returnFormControllerService.deleteReturnForm(returnFormId);
	}

	// added by kaleb
	@RequestMapping(value = "/offline", method = RequestMethod.GET)
	@PreAuthorize("hasRole('" + PermissionConstants.VIEW_DONOR + "')")
	public ResponseEntity<Map<String, Object>> findOfflineForms() {
		Map<String, Object> m = new HashMap<String, Object>();
		///
		List<Map<String, Object>> venueViewModels = new ArrayList<Map<String, Object>>();
		List<Location> locations = locationRepository.getVenues();
		if (locations != null) {
			for (Location location : locations) {
				Map<String, Object> viewModel = new HashMap<String, Object>();
				viewModel.put("id", location.getId());
				viewModel.put("name", location.getName());
				viewModel.put("isDeleted", location.getIsDeleted());
				viewModel.put("isProcessingSite", location.getIsProcessingSite());
				viewModel.put("isMobileSite", location.getIsMobileSite());
				viewModel.put("isVenue", location.getIsVenue());
				viewModel.put("isDistributionSite", location.getIsDistributionSite());
				viewModel.put("isUsageSite", location.getIsUsageSite());
				viewModel.put("isReferralSite", location.getIsReferralSite());
				viewModel.put("isTestingSite", location.getIsTestingSite());

				venueViewModels.add(viewModel);
			}
		}

		///
		m.put("venues", venueViewModels);
		// m.put("venues",
		// locationFactory.createFullViewModels(locationRepository.getVenues()));
		List<DonationType> donationTypes = donorTypeRepository.getAllDonationTypes();
		m.put("donationTypes", donationTypeFactory.createViewModels(donationTypes));
		m.put("packTypes", packTypeFactory.createFullViewModels(packTypeRepository.getAllEnabledPackTypes()));
		m.put("adverseEventTypes", adverseEventTypeRepository.findNonDeletedAdverseEventTypeViewModels());
		List<DonationBatch> donationBatches = donationBatchRepository.findOfflineDonationBatches();
		///
		List<Map<String, Object>> donationBatchViewModels = new ArrayList<Map<String, Object>>();
		if (donationBatches != null) {
			for (DonationBatch donationBatch : donationBatches) {
				Map<String, Object> viewModel = new HashMap<String, Object>();
				viewModel.put("id", donationBatch.getId());
				viewModel.put("batchNumber", donationBatch.getBatchNumber());
				viewModel.put("notes", donationBatch.getNotes());
				viewModel.put("donationBatchDate",
						CustomDateFormatter.getDateTimeString(donationBatch.getDonationBatchDate()));
				// venue
				viewModel.put("venue", new LocationFullViewModel(donationBatch.getVenue()));
				viewModel.put("backEntry", donationBatch.isBackEntry());
				viewModel.put("numDonations", donationBatch.getDonations().size());
				viewModel.put("status", donationBatch.getIsClosed() == true ? "CLOSED" : "OPEN");
				User createdBy = donationBatch.getCreatedBy();
				User lastUpdatedBy = donationBatch.getLastUpdatedBy();
				viewModel.put("createdBy", createdBy == null ? "" : createdBy.getUsername());
				viewModel.put("isClosed", donationBatch.getIsClosed());
				viewModel.put("lastUpdatedBy", lastUpdatedBy == null ? "" : lastUpdatedBy.getUsername());
				viewModel.put("lastUpdated", CustomDateFormatter.getDateTimeString(donationBatch.getLastUpdated()));
				viewModel.put("isDeleted", donationBatch.getIsDeleted());
				donationBatchViewModels.add(viewModel);
			}
		}

		///
		m.put("donationBatches", donationBatchViewModels);
		// m.put("donationBatches",
		// donationBatchViewModelFactory.createDonationBatchBasicViewModels(donationBatches));
		m.put("languages", donorRepository.getAllLanguages());
		m.put("deferralReasons", deferralControllerService.getDeferralReasons());
		m.put("preferredContactMethods", contactMethodTypeRepository.getAllContactMethodTypes());
		m.put("addressTypes", donorRepository.getAllAddressTypes());
		return new ResponseEntity<Map<String, Object>>(m, HttpStatus.OK);
	}

	// added by kaleb

}