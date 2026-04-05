import {
  AngularNodeAppEngine,
  createNodeRequestHandler,
  isMainModule,
  writeResponseToNodeResponse,
} from '@angular/ssr/node';
import express from 'express';
import { join } from 'node:path';

const browserDistFolder = join(import.meta.dirname, '../browser');

const app = express();
const angularApp = new AngularNodeAppEngine();

/**
 * Example Express Rest API endpoints can be defined here.
 * Uncomment and define endpoints as necessary.
 *
 * Example:
 * ```ts
 * app.get('/api/{*splat}', (req, res) => {
 *   // Handle API request
 * });
 * ```
 */

/**
  Serve static files from /browser

"Serve any physical files found in the browser build folder 
immediately. Tell browsers to cache them for a long time. 
However, do not serve the index.html file automatically—let 
the Angular SSR engine handle that later so we can pre-render 
the page."
*/
app.use(
  express.static(browserDistFolder, {
    maxAge: '1y',
    index: false,
    redirect: false,
  }),
);

/**
 * Handle all other requests by rendering the Angular application.
This defines a middleware function that runs for every 
incoming request that hasn't already been answered 
(e.g., by the static file handler above it).
 */

/*
angularApp.handle(req)
The Engine: angularApp is an instance of 
AngularNodeAppEngine.
The Action: It takes the incoming Node.js request (req) 
and boots up your Angular application on the server.
The Goal: It tries to match the URL (e.g., /dashboard) 
to an Angular route, generate the full HTML for that 
page (Server-Side Rendering), and return it.

The .then((response) => ...) Logic
The handle method returns a Promise. When it finishes, 
it gives back a response object.
response ? ... (Success):



*/
app.use((req, res, next) => {
  angularApp
    .handle(req)
    .then((response) =>
      response ? writeResponseToNodeResponse(response, res) : next(),
/*
writeResponseToNodeResponse(response, res): This helper 
function takes the internal Angular response and writes 
it out to the actual Node.js res object, sending the HTML 
back to the user's browser.

.catch(next)
If the Angular rendering engine crashes or throws an error 
during the process, this catches the error.
It passes the error to next(error), which triggers Express's 
default error handling (usually printing the error stack trace).
*/
    )
    .catch(next);
});

/**
 * Start the server if this module is the main entry point, 
 * or it is ran via PM2.
 * The server listens on the port defined by the `PORT` 
 * environment variable, or defaults to 4000.
 */

/*
This code block is responsible for starting the web server 
so it can accept incoming connections. It includes logic to 
ensure the server only starts when intended.
(not when imported by other tools) and selects the correct 
port.

if (isMainModule(import.meta.url) || process.env['pm_id']) 
{ ... }


isMainModule(import.meta.url): This checks if this 
specific file is the entry point of the application 
(e.g., run via node server.js).

process.env['pm_id']: This checks if the application was 
launched by PM2, a popular process manager for Node.js 
production deployments.

Why? This prevents the server from trying to open a port 
if this file is simply being imported by another script 
(like a testing framework or a Firebase Cloud Function handler),
which would cause errors.
*/
if (isMainModule(import.meta.url) || process.env['pm_id']) {
  const port = process.env['PORT'] || 4000;
  app.listen(port, (error) => {
    if (error) {
      throw error;
    }

    console.log(`Node Express server listening on http://localhost:${port}`);
  });
}

/**
 * Request handler used by the Angular CLI (for dev-server and during build) or Firebase Cloud Functions.
 */
export const reqHandler = createNodeRequestHandler(app);
