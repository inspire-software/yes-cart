/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package controls {
	import flash.display.Sprite;

	import mx.effects.Fade;

	public class Tick extends Sprite {
		private var tickFade:Fade = new Fade(this);


		public function Tick(fromX:Number, fromY:Number, toX:Number, toY:Number, tickWidth:int, tickColor:uint) {
			this.graphics.lineStyle(tickWidth, tickColor, 1.0, false, "normal", "rounded");
			this.graphics.moveTo(fromX, fromY);
			this.graphics.lineTo(toX, toY);
		}


		public function fade(duration:Number):void {
			tickFade.alphaFrom = 1.0;
			tickFade.alphaTo = 0.1;
			tickFade.duration = duration;
			tickFade.play();
		}
	}
}