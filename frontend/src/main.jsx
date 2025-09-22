import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.jsx'
import Prototype from "./Prototype.jsx";

createRoot(document.getRootNode()).render(
  <StrictMode>
    <Prototype />
  </StrictMode>,
)
