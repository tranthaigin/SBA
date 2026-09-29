function Footer() {
  const year = new Date().getFullYear();

  return (
    <footer className="footer">
      <p>SBA301 • {year} • Build → Run → Verify → Explain</p>
    </footer>
  );
}

export default Footer;
