// 1. Import the Angular bootstrapper capable of launching a Standalone app
import { bootstrapApplication } from '@angular/platform-browser';

// 2. Import global configurations like routing rules and HTTP client setup
import { appConfig } from './app/app.config';

// 3. Import the root component which holds the <router-outlet> and main layout
import { AppComponent } from './app/app';

/*
 * HOW MAIN.TS WORKS:
 *
 * 1. The browser loads 'index.html', which contains the empty <app-root></app-root> tag.
 * 2. 'main.ts' is executed immediately after.
 * 3. The 'bootstrapApplication' function is called, telling Angular:
 *    "Start the application using 'AppComponent' as the main visual shell,
 *    and configure the app's services using the settings inside 'appConfig'."
 * 4. If the app fails to start (e.g., due to a broken configuration),
 *    the .catch() block safely catches the error and logs it to the console.
 */
bootstrapApplication(AppComponent, appConfig)
  .catch((err) => console.error(err));

