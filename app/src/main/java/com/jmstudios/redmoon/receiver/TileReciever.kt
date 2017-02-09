/* Created by cj on 12/01/17.
 *
 *  This file is free software: you may copy, redistribute and/or modify it
 *  under the terms of the GNU General Public License as published by the Free
 *  Software Foundation, either version 3 of the License, or (at your option)
 *  any later version.
 *
 *  This file is distributed in the hope that it will be useful, but WITHOUT ANY
 *  WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 *  FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 *  details.
 *
 *  You should have received a copy of the GNU General Public License along with
 *  this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.jmstudios.redmoon.receiver

import android.annotation.TargetApi
import android.service.quicksettings.TileService

import com.jmstudios.redmoon.activity.ShortcutToggleActivity

@TargetApi(24)
class TileReciever : TileService() {
    override fun onClick() {
        super.onClick()

        ShortcutToggleActivity.toggleAndFinish()
    }
}
