'use strict';
module.exports = { id: 'reny', capabilities: ['profiler.read'], tools: [{
  name: 'reny_get_profiler_snapshot', description: 'Read the immutable Reny profiler snapshot',
  path: '/reny/profiler', inputSchema: { type: 'object', properties: {}, additionalProperties: false }
}] };
