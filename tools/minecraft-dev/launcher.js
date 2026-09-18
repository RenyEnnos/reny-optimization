'use strict';
const fs = require('node:fs');
const path = require('node:path');
const home = process.env.MINECRAFT_DEV_TOOLKIT_HOME || path.resolve(__dirname, '../../../minecraft-dev-toolkit');
const entry = path.join(home, 'mcp', 'src', 'index.js');
if (!fs.existsSync(entry)) throw new Error('minecraft-dev-toolkit MCP not found; set MINECRAFT_DEV_TOOLKIT_HOME or provide sibling minecraft-dev-toolkit');
process.env.MINECRAFT_DEV_ADAPTERS = [process.env.MINECRAFT_DEV_ADAPTERS, path.join(__dirname, 'adapter.js')].filter(Boolean).join(path.delimiter);
require(entry).main();
