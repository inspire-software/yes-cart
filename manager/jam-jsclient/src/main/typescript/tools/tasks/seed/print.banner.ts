import Config from '../../config';
import { readFile } from 'fs';
import * as util from 'gulp-util';
import { join } from 'path';

export = (done: any) => {
  let bannerPath = join(Config.TOOLS_DIR, 'config', 'cwbanner.txt'); // #CUSTOM#
  if (require('supports-color').has256) {
    bannerPath = join(Config.TOOLS_DIR, 'config', 'cwbanner-256.txt'); // #CUSTOM#
  }
  readFile(bannerPath, (e, content) => {
    if (!e) {
      console.log(util.colors.blue(content.toString()));
    }
    done();
  });
};

