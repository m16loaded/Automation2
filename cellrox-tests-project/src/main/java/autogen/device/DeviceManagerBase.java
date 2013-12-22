package autogen.device;
import com.cellrox.infra.CellRoxDevice;
import junit.framework.SystemTestCase;
/**
 * Auto generate management object.
 * Managed object class: com.cellrox.infra.CellRoxDevice
 * This file <b>shouldn't</b> be changed, to overwrite methods behavier
 * change: DeviceManager.java
 */
public abstract class DeviceManagerBase extends SystemTestCase{
	protected CellRoxDevice device = null;
	public void setUp() throws Exception {
		device = (CellRoxDevice)system.getSystemObject("device");
	}
}
