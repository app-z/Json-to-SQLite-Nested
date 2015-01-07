package airoports;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class Airport {

	@Expose
	private String code;
	@Expose
	private String name;
	@Expose
	private Coordinates coordinates;
	@SerializedName("time_zone")
	@Expose
	private String timeZone;
	@SerializedName("name_translations")
	@Expose
	private NameTranslations nameTranslations;
	@SerializedName("country_code")
	@Expose
	private String countryCode;
	@SerializedName("city_code")
	@Expose
	private String cityCode;

	/**
	 * 
	 * @return The code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * 
	 * @param code
	 *            The code
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * 
	 * @return The name
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @param name
	 *            The name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 
	 * @return The coordinates
	 */
	public Coordinates getCoordinates() {
		return coordinates;
	}

	/**
	 * 
	 * @param coordinates
	 *            The coordinates
	 */
	public void setCoordinates(Coordinates coordinates) {
		this.coordinates = coordinates;
	}

	/**
	 * 
	 * @return The timeZone
	 */
	public String getTimeZone() {
		return timeZone;
	}

	/**
	 * 
	 * @param timeZone
	 *            The time_zone
	 */
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	/**
	 * 
	 * @return The nameTranslations
	 */
	public NameTranslations getNameTranslations() {
		return nameTranslations;
	}

	/**
	 * 
	 * @param nameTranslations
	 *            The name_translations
	 */
	public void setNameTranslations(NameTranslations nameTranslations) {
		this.nameTranslations = nameTranslations;
	}

	/**
	 * 
	 * @return The countryCode
	 */
	public String getCountryCode() {
		return countryCode;
	}

	/**
	 * 
	 * @param countryCode
	 *            The country_code
	 */
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	/**
	 * 
	 * @return The cityCode
	 */
	public String getCityCode() {
		return cityCode;
	}

	/**
	 * 
	 * @param cityCode
	 *            The city_code
	 */
	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

}