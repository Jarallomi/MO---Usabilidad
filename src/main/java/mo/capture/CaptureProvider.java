package mo.capture;

import mo.core.plugin.ExtensionPoint;
import mo.organization.StagePlugin;

/**
 * Extensions that provide capture features must implement this interface
 */
@ExtensionPoint
public interface CaptureProvider extends StagePlugin {

}
