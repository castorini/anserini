import '../styles/globals.css'
import SearchBar from './components/SearchBar'

export default function Home() {
  return (
    <div className="container">
      <div className="header">
        <h1>Anserini Search Interface</h1>
        <p>A Lucene toolkit for reproducible information retrieval research</p>
      </div>
      <SearchBar />
    </div>
  );
}
