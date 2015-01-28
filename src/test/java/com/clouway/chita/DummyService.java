package com.clouway.chita;

/**
 * @author Tsony Tsonev (tsony.tsonev@clouway.com)
 */
public class DummyService {
  private final String id;
  private final String name;
  private final String status;

  public DummyService(String id, String name, String status) {
    this.id = id;
    this.name = name;
    this.status = status;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    DummyService that = (DummyService) o;

    if (id != null ? !id.equals(that.id) : that.id != null) return false;
    if (name != null ? !name.equals(that.name) : that.name != null) return false;
    if (status != null ? !status.equals(that.status) : that.status != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = id != null ? id.hashCode() : 0;
    result = 31 * result + (name != null ? name.hashCode() : 0);
    result = 31 * result + (status != null ? status.hashCode() : 0);
    return result;
  }
}
