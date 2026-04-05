import { RenderMode, ServerRoute } from '@angular/ssr';

export const serverRoutes: ServerRoute[] = [
  {

    /*
    path: '**': This means the rule applies to all pages 
    in your application.
    */
    path: '**',
    //renderMode: RenderMode.Prerender: This tells Angular 
    // to Prerender these pages.


    /*
    rerendering (SSG): Angular will generate static HTML 
    files for your pages at build time (when you run ng build). 
    This is great for performance and SEO because the browser 
    receives a full HTML page immediately, rather than waiting for JavaScript to run.
    
    In summary: This file is telling Angular: 
    "When you build my app, please try to generate 
     static HTML files for every route you can find."
    */
    renderMode: RenderMode.Prerender
  }
];
