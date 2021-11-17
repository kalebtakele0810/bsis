package org.jembi.bsis.viewmodel;

import java.util.Map;

import org.jembi.bsis.repository.DonorRepository;

/**
 * View model representing a summarised offline view of a donor.
 */
public class DonorOfflineViewModel {

	private Map<String, Object> overview;
	private Map<String, Object> donations;
	private Map<String, Object> deferrals;
	private Map<String, Object> donor;

	public DonorOfflineViewModel() {
		// no-args constructor
	}

	/**
	 * Do not remove or change the signature of this method.
	 *
	 * @see {@link DonorRepository#findDonorSummaryByDonorNumber(String)}
	 */
	public DonorOfflineViewModel(Map<String, Object> overview, Map<String, Object> donations,
			Map<String, Object> deferrals, Map<String, Object> donor) {
		this.overview = overview;
		this.donations = donations;
		this.deferrals = deferrals;
		this.donor = donor;

	}

	public Map<String, Object> getOverview() {
		return overview;
	}

	public void setOverview(Map<String, Object> overview) {
		this.overview = overview;
	}

	public Map<String, Object> getDonations() {
		return donations;
	}

	public void setDonations(Map<String, Object> donations) {
		this.donations = donations;
	}

	public Map<String, Object> getDeferrals() {
		return deferrals;
	}

	public void setDeferrals(Map<String, Object> deferrals) {
		this.deferrals = deferrals;
	}

	public Map<String, Object> getDonor() {
		return donor;
	}

	public void setDonor(Map<String, Object> donor) {
		this.donor = donor;
	}

}
